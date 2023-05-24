package com.lkd.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Strings;
import com.lkd.dao.SkuDao;
import com.lkd.entity.SkuClassEntity;
import com.lkd.entity.SkuEntity;
import com.lkd.service.SkuClassService;
import com.lkd.service.SkuService;
import com.lkd.http.controller.vo.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SkuServiceImpl extends ServiceImpl<SkuDao, SkuEntity> implements SkuService {


}
