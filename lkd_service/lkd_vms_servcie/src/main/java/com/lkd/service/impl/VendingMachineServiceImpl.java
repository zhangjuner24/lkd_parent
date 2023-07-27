package com.lkd.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkd.common.VMSystem;
import com.lkd.dao.VendingMachineDao;
import com.lkd.entity.*;
import com.lkd.http.controller.vo.Pager;
import com.lkd.service.*;
import com.lkd.utils.UUIDUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class VendingMachineServiceImpl extends
        ServiceImpl<VendingMachineDao, VendingMachineEntity>
        implements VendingMachineService {

    //售货机
    @Autowired
    private VendingMachineService vendingMachineService;
    //区域
    @Autowired
    private RegionService regionService;

    @Autowired
    private NodeService nodeService;

    @Override
    public Pager<VendingMachineEntity> fenye(Integer pageIndex, Integer pageSize, Integer status, String innerCode) {
        Page<VendingMachineEntity> entityPage = new Page<>(pageIndex, pageSize);
        //分页条件,
        LambdaQueryWrapper<VendingMachineEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ObjectUtil.isNotEmpty(status), VendingMachineEntity::getStatus, status);
        queryWrapper.eq(StrUtil.isNotEmpty(innerCode), VendingMachineEntity::getInnerCode, innerCode);
        this.page(entityPage, queryWrapper);

        //遍历售货机列表

        for (VendingMachineEntity record : entityPage.getRecords()) {
/*            LambdaQueryWrapper<VendingMachineEntity> ss = new LambdaQueryWrapper<>();
            ss.eq(VendingMachineEntity::getVmType, record.getVmType());
            VmTypeEntity one = vmTypeService.getById(ss);
            record.setVmType(one.getTypeId());*/
            // 查询售货机类型对象
            VmTypeEntity byId1 = vmTypeService.getById(record.getVmType());
            record.setVmType(byId1.getTypeId());


            //查询所在区域model
            LambdaQueryWrapper<RegionEntity> rQuery = new LambdaQueryWrapper<>();
            rQuery.eq(RegionEntity::getId,record.getRegionId());
            RegionEntity byId = regionService.getOne(rQuery);
            record.setRegion(byId);

            //查询点位名称
/*            LambdaQueryWrapper<NodeEntity> lan = new LambdaQueryWrapper<>();
            lan.eq(NodeEntity::getId,record.getNodeId());*/


            NodeEntity dianwei = nodeService.getById(record.getNodeId());
            record.setNode(dianwei);

        }
        return Pager.build(entityPage);
    }




    @Autowired
    private VmTypeService vmTypeService;

    @Autowired
    private ChannelService channelService;

    // 新增售货机
    @Override
    @Transactional
    public Boolean addVM(VendingMachineEntity vendingMachineEntity) {
        // 1.保存售货机基本信息
        // 1-1 补充信息
        String innerCode = UUIDUtils.getUUID();
        vendingMachineEntity.setInnerCode(innerCode);
        vendingMachineEntity.setVmStatus(VMSystem.VM_STATUS_NODEPLOY);
        // 查询点位
        NodeEntity nodeEntity = nodeService.getById(vendingMachineEntity.getNodeId());
        BeanUtil.copyProperties(nodeEntity, vendingMachineEntity);
        vendingMachineEntity.setClientId(UUIDUtils.generateClientId(innerCode));
        // 1-2 保存
        this.save(vendingMachineEntity);

        // 2.关联保存该售货机货道信息
        // 2-1 先查询售货机类型
        VmTypeEntity vmTypeEntity = vmTypeService.getById(vendingMachineEntity.getVmType());
        // 2-2 双重for循环  行+列
        List<ChannelEntity> channelList = new ArrayList<>();
        for (int i = 1; i <= vmTypeEntity.getVmRow(); i++) {
            for (int j = 1; j <= vmTypeEntity.getVmCol(); j++) {
                // 创建货道对象
                ChannelEntity channelEntity = new ChannelEntity();
                channelEntity.setChannelCode(i + "-" + j);
                channelEntity.setVmId(vendingMachineEntity.getId());
                channelEntity.setInnerCode(vendingMachineEntity.getInnerCode());
                channelEntity.setMaxCapacity(vmTypeEntity.getChannelMaxCapacity());
                // 保存
                // channelService.save(channelEntity);
                channelList.add(channelEntity);
            }
        }
        // 批量保存
        return channelService.saveBatch(channelList);
    }
}
