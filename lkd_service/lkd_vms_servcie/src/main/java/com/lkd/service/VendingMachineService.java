package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.entity.VendingMachineEntity;
import com.lkd.http.controller.vo.Pager;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author LKD
 */
public interface VendingMachineService extends IService<VendingMachineEntity> {


    Pager<VendingMachineEntity> fenye(Integer pageIndex, Integer pageSize, Integer status, String innerCode);


    // 新增售货机
    Boolean addVM(VendingMachineEntity vendingMachineEntity);
}
