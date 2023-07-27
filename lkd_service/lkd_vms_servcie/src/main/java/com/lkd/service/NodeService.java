package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.entity.NodeEntity;
import com.lkd.entity.VendingMachineEntity;
import com.lkd.http.controller.vo.Pager;

import java.util.List;

/**
 * <p>
 * 点位表 服务类
 * </p>
 *
 * @author LKD
 */
public interface NodeService extends IService<NodeEntity> {


    Pager<NodeEntity> search(Integer pageIndex, Integer pageSize, String name, Long regionId);

    // 点位详情
    List<VendingMachineEntity> nodeDetail(Long id);
}
