package com.lkd.http.controller.vo;

import lombok.Data;

@Data
public class CreateVMReq {
    /**
     * 售货机类型
     */
    private int vmType;
    /**
     * 所属点位Id
     */
    private String  nodeId;
    /**
     * 创建人Id
     */
    private Integer createUserId;
}