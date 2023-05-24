package com.lkd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkd.dao.VendingMachineDao;
import com.lkd.entity.VendingMachineEntity;
import com.lkd.service.NodeService;
import com.lkd.service.RegionService;
import com.lkd.service.VendingMachineService;
import com.lkd.service.VmTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class VendingMachineServiceImpl extends ServiceImpl<VendingMachineDao, VendingMachineEntity> implements VendingMachineService {


}
