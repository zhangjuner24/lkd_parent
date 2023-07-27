package com.lkd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lkd.entity.ChannelEntity;

import java.util.List;

/**
 * <p>
 * 售货机货道表 服务类
 * </p>
 *
 * @author LKD
 */
public interface ChannelService extends IService<ChannelEntity> {

    // 根据售货机编号查询货道列表
    List<ChannelEntity> channelList(String innerCode);
}
