package com.lkd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.lkd.common.VMSystem;
import com.lkd.dao.TaskDao;
import com.lkd.entity.TaskEntity;
import com.lkd.entity.TaskStatusTypeEntity;
import com.lkd.exception.LogicException;
import com.lkd.feign.UserServiceFeignClient;
import com.lkd.http.controller.vo.Pager;
import com.lkd.http.controller.vo.UserWorkVO;
import com.lkd.http.vo.TaskCollectVO;
import com.lkd.http.vo.TaskReportInfoVO;
import com.lkd.http.vo.TaskViewModel;
import com.lkd.service.TaskDetailsService;
import com.lkd.service.TaskService;
import com.lkd.service.TaskStatusTypeService;
import com.lkd.service.TaskTypeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskServiceImpl extends ServiceImpl<TaskDao, TaskEntity> implements TaskService {

    @Autowired
    private TaskStatusTypeService taskStatusTypeService;

    @Autowired
    private TaskTypeService taskTypeService;
    @Autowired
    private UserServiceFeignClient userServiceFeignClient;
    @Autowired
    private TaskDetailsService taskDetailsService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 查询所有工单状态
    @Override
    public List<TaskStatusTypeEntity> getAllStatus() {
        return taskStatusTypeService.list();
    }

    // 搜索工单
    @Override
    public Pager<TaskEntity> search(Long pageIndex, Long pageSize, String innerCode, Integer userId, String taskCode, Integer status, Boolean isRepair, String start, String end) {

        return null;
    }


    // 获取人员排名
    @Override
    public List<UserWorkVO> getUserWorkTop10(LocalDate startTime, LocalDate endTime, Boolean isRepair, Long regionId) {
        // select
        //     user_name,
        //     count(*) user_id
        // from
        //     tb_task
        // where
        //     task_status = 4
        //     and
        //     update_time >= '2022-12-01 00:00:00'
        //     and
        //     update_time < '2023-01-01 00:00:00'
        //     and
        //     region_id = ?
        //     and
        //     product_type_id = ?
        // group by user_name
        // order by num desc
        // limit 10

        QueryWrapper<TaskEntity> queryWrapper = new QueryWrapper<TaskEntity>();
        queryWrapper.select(" user_name,count(*) user_id ");
        queryWrapper.last("order by user_id desc  limit 10");
        queryWrapper.lambda()
                .eq(TaskEntity::getTaskStatus, VMSystem.TASK_STATUS_FINISH)
                .ge(TaskEntity::getUpdateTime, startTime)
                .lt(TaskEntity::getUpdateTime, endTime.plusDays(1))
                .groupBy(TaskEntity::getUserName);

        if (regionId > 0) {
            queryWrapper.lambda().eq(TaskEntity::getRegionId, regionId);
        }
        if (isRepair) {
            queryWrapper.lambda().ne(TaskEntity::getProductTypeId, VMSystem.TASK_TYPE_SUPPLY);
        } else {
            queryWrapper.lambda().eq(TaskEntity::getProductTypeId, VMSystem.TASK_TYPE_SUPPLY);
        }

        List<TaskEntity> resultList = this.list(queryWrapper);

        // 将 List<TaskEntity> 变为 List<UserWorkVO>
        List<UserWorkVO> userWorks = resultList.stream().map(taskEntity -> {
            var userWorkVO = new UserWorkVO();
            userWorkVO.setUserName(taskEntity.getUserName());
            userWorkVO.setWorkCount(taskEntity.getUserId());
            return userWorkVO;
        }).collect(Collectors.toList());

        return userWorks;
    }

    /**
     * 获取工单报表
     *
     * @param start
     * @param end
     * @return
     */
    @Override
    public List<TaskCollectVO> getTaskReport(LocalDate start, LocalDate end) {
        List<TaskCollectVO> taskCollectVOList = Lists.newArrayList();
        //从开始日期到截至日期，逐条统计
        start.datesUntil(end.plusDays(1), Period.ofDays(1))
                .forEach(date -> {
                    var taskCollectVO = new TaskCollectVO();
                    taskCollectVO.setCollectDate(date);
                    taskCollectVO.setProgressCount(count(date, VMSystem.TASK_STATUS_PROGRESS)); //进行中工单数
                    taskCollectVO.setFinishCount(count(date, VMSystem.TASK_STATUS_FINISH));//完成工单数
                    taskCollectVO.setCancelCount(count(date, VMSystem.TASK_STATUS_CANCEL));//取消工单数
                    taskCollectVOList.add(taskCollectVO);
                });
        return taskCollectVOList;
    }

    /**
     * 获取用户工作量详情
     *
     * @param userId
     * @param start
     * @param end
     * @return
     */
    @Override
    public UserWorkVO getUserWork(Integer userId, LocalDateTime start, LocalDateTime end) {
        var userWork = new UserWorkVO();
        userWork.setUserId(userId);

        //获取用户完成工单数
        var workCount = this.getCountByUserId(userId, VMSystem.TASK_STATUS_FINISH, start.toLocalDate(), end);
        userWork.setWorkCount(workCount);

        //获取工单总数
        var total = this.getCountByUserId(userId, null, start.toLocalDate(), end);
        userWork.setTotal(total);

        //获取用户拒绝工单数
        var cancelCount = this.getCountByUserId(userId, VMSystem.TASK_STATUS_CANCEL, start.toLocalDate(), end);
        userWork.setCancelCount(cancelCount);

        //获取进行中得工单数
        var progressTotal = this.getCountByUserId(userId, VMSystem.TASK_STATUS_PROGRESS, start.toLocalDate(), end);
        userWork.setProgressTotal(progressTotal);

        return userWork;
    }

    //根据工单状态，获取用户指定时间段范围的工单数
    private Integer getCountByUserId(Integer userId, Integer taskStatus, LocalDate start, LocalDateTime end) {
        var qw = new LambdaQueryWrapper<TaskEntity>();
        qw
                .ge(TaskEntity::getUpdateTime, start)
                .le(TaskEntity::getUpdateTime, end);
        if (taskStatus != null) {
            qw.eq(TaskEntity::getTaskStatus, taskStatus);
        }
        if (userId != null) {
            qw.eq(TaskEntity::getUserId, userId);
        }
        return this.count(qw);
    }

    /**
     * 按时间和状态进行统计
     *
     * @param start
     * @param taskStatus
     * @return
     */
    private int count(LocalDate start, Integer taskStatus) {
        var qw = new LambdaQueryWrapper<TaskEntity>();
        qw
                .ge(TaskEntity::getUpdateTime, start)
                .lt(TaskEntity::getUpdateTime, start.plusDays(1))
                .eq(TaskEntity::getTaskStatus, taskStatus);
        return this.count(qw);
    }





    /**
     * 生成工单编号
     *
     * @return
     */
    private String generateTaskCode() {
        //      日期字符串
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//        把当前序号存入redis中 ，key的值
        String key = "lkd:task:code:" + date;
        Long value = redisTemplate.opsForValue().increment(key);
        if (value == 1) {
//            只在第一次操作redis的时候，设置有效期
            redisTemplate.expire(key, 1, TimeUnit.DAYS);
        }
//        返回的工单号 日期+序号 ，序号如果不够4位，前面补0
        return date + Strings.padStart(value.toString(), 4, '0');
    }

    /**
     * 判断工单基础数据
     **/
    private void verifyTaskMsg(TaskViewModel taskViewModel) {

        if (StringUtils.isEmpty(taskViewModel.getInnerCode())) {
            throw new LogicException("售货机编号不能为空");
        }

        if (ObjectUtils.isEmpty(taskViewModel.getUserId())) {
            throw new LogicException("必须要指定员工");

        }
        if (ObjectUtils.isEmpty(taskViewModel.getProductType())) {
            throw new LogicException("工单类型不能为空");
        }

        if (StringUtils.isEmpty(taskViewModel.getDesc())) {
            throw new LogicException("工单描述不能为空");
        }

        if (VMSystem.TASK_TYPE_SUPPLY == taskViewModel.getProductType()) {
            if (CollectionUtils.isEmpty(taskViewModel.getDetails())) {
                throw new LogicException("补货工单补货详情不能为空");
            }
        }
    }

    /**
     * 创建工单校验
     *
     * @param vmStatus
     * @param productType
     * @throws LogicException
     */
    private void checkCreateTask(Integer vmStatus, int productType) throws LogicException {
        //如果是投放工单，状态为运营
        if (productType == VMSystem.TASK_TYPE_DEPLOY && vmStatus == VMSystem.VM_STATUS_RUNNING) {
            throw new LogicException("该设备已在运营");
        }

        //如果是补货工单，状态不是运营状态
        if (productType == VMSystem.TASK_TYPE_SUPPLY && vmStatus != VMSystem.VM_STATUS_RUNNING) {
            throw new LogicException("该设备不在运营状态");
        }

        //如果是撤机工单，状态不是运营状态
        if (productType == VMSystem.TASK_TYPE_REVOKE && vmStatus != VMSystem.VM_STATUS_RUNNING) {
            throw new LogicException("该设备不在运营状态");
        }
    }

    /**
     * 同一台设备下是否存在未完成的工单
     *
     * @param innerCode
     * @param productionType
     * @return
     */
    private boolean hasTask(String innerCode, int productionType) {
        QueryWrapper<TaskEntity> qw = new QueryWrapper<>();
        qw.lambda()
                .select(TaskEntity::getTaskId)
                .eq(TaskEntity::getInnerCode, innerCode)
                .eq(TaskEntity::getProductTypeId, productionType)
                .le(TaskEntity::getTaskStatus, VMSystem.TASK_STATUS_PROGRESS);
        return this.count(qw) > 0;
    }

}
