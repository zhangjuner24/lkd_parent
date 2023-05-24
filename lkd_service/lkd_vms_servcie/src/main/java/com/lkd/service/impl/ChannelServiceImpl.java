package com.lkd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkd.dao.ChannelDao;
import com.lkd.http.controller.vo.ChannelConfigReq;
import com.lkd.entity.ChannelEntity;
import com.lkd.entity.SkuEntity;
import com.lkd.exception.LogicException;
import com.lkd.service.ChannelService;
import com.lkd.service.SkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelServiceImpl extends ServiceImpl<ChannelDao, ChannelEntity> implements ChannelService {



}
