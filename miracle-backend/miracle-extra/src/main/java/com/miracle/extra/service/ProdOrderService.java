package com.miracle.extra.service;


import com.miracle.extra.domain.ProdOrder;

/**
 * 生产工单 Service 接口
 */
public interface ProdOrderService {

    /**
     * 根据工单号查询工单信息
     * @param productionOrderNo 生产工单号
     * @return 工单信息
     */
    ProdOrder getByOrderNo(String productionOrderNo);
}