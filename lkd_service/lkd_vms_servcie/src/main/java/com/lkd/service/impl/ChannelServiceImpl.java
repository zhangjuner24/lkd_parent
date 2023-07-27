package com.lkd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lkd.dao.ChannelDao;
import com.lkd.entity.ChannelEntity;
import com.lkd.service.ChannelService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelServiceImpl extends ServiceImpl<ChannelDao, ChannelEntity> implements ChannelService {


    // 根据售货机编号查询货道列表
    @Override
    public List<ChannelEntity> channelList(String innerCode) {


        return null;
    }
}
