package com.lkd.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkd.dao.SkuDao;
import com.lkd.entity.SkuEntity;
import com.lkd.http.controller.vo.Pager;
import com.lkd.service.SkuClassService;
import com.lkd.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SkuServiceImpl extends ServiceImpl<SkuDao, SkuEntity> implements SkuService {

    @Autowired
    private SkuClassService skuClassService;


    //商品搜索
    @Override
    public Pager<SkuEntity> search(Integer pageIndex, Integer pageSize, String skuName, Long classId) {
        //先查询商品信息,分页
        Page<SkuEntity> page = new Page<>(pageIndex, pageSize);
        //分页的条件
        LambdaQueryWrapper<SkuEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .like(ObjectUtil.isNotEmpty(skuName), SkuEntity::getSkuName, skuName)
                .eq(ObjectUtil.isNotEmpty(classId), SkuEntity::getClassId, classId);

        //调用父类的方法查询
        this.page(page, queryWrapper);
        //遍历商品列表设置进去
/*        for (SkuEntity sku : page.getRecords()) {
            SkuClassEntity byId = skuClassService.getById(sku.getClassId());
            sku.setSkuClass(byId);
        }*/


        return Pager.build(page);
    }
}
