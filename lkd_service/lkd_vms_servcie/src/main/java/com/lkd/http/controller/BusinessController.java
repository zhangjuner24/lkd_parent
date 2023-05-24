package com.lkd.http.controller;

import com.lkd.entity.BusinessTypeEntity;
import com.lkd.service.BusinessTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
public class BusinessController {
    @Autowired
    private BusinessTypeService businessTypeService;

    /**
     * 获取所有商圈
     * @return List<BusinessEntity>
     */
    @GetMapping("/businessType")
    public List<BusinessTypeEntity> getAll(){
        return businessTypeService.list();
    }




}
