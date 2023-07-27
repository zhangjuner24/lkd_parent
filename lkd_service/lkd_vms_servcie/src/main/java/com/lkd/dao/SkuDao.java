package com.lkd.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lkd.entity.SkuEntity;
import org.apache.ibatis.annotations.*;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author LKD
 */
@Mapper
public interface SkuDao extends BaseMapper<SkuEntity> {

    @Results(id = "skuMap" ,value = {
            @Result(column = "sku_id", property = "skuId",id = true),
            @Result(column = "class_id", property = "skuClass",
                    one = @One(select = "com.lkd.dao.SkuClassDao.selectById")),

    })
    @Select("select * from tb_sku where sku_id=#{skuId}")
    public SkuEntity getById(Long skuId);

}
