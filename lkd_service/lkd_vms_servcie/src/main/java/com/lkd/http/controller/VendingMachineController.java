package com.lkd.http.controller;

import com.lkd.entity.VendingMachineEntity;
import com.lkd.http.controller.vo.Pager;
import com.lkd.service.VendingMachineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/vm")
public class VendingMachineController {

    @Autowired
    private VendingMachineService vendingMachineService;

    //售货机分页查询
    @GetMapping("/search")
    public Pager<VendingMachineEntity> fenye(
            @RequestParam(defaultValue = "1") Integer pageIndex,
            @RequestParam(defaultValue = "10")Integer pageSize,
            Integer status,
            String innerCode
            ){
        return vendingMachineService.fenye(pageIndex,pageSize,status,innerCode);

    }

    // 新增售货机
    @PostMapping
    public Boolean addVM(@RequestBody VendingMachineEntity vendingMachineEntity){
        return vendingMachineService.addVM(vendingMachineEntity);
    }
}