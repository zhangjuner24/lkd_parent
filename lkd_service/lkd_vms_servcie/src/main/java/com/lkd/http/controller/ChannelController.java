package com.lkd.http.controller;

import com.lkd.entity.ChannelEntity;
import com.lkd.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/channel")
public class ChannelController {

    @Autowired
    private ChannelService channelService;



    // 根据售货机编号查询货道列表
    @GetMapping("/channelList/{innerCode}")
    public List<ChannelEntity> channelList(@PathVariable("innerCode") String innerCode){
        return channelService.channelList(innerCode);
    }
}