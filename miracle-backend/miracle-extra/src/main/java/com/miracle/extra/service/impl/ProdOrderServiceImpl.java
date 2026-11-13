package com.miracle.extra.service.impl;


import com.miracle.extra.domain.ProdOrder;
import com.miracle.extra.mapper.ProdOrderMapper;
import com.miracle.extra.service.ProdOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 生产工单 Service 实现类
 */
@Service
public class ProdOrderServiceImpl implements ProdOrderService {

    @Autowired
    private ProdOrderMapper prodOrderMapper;

    @Override
    public ProdOrder getByOrderNo(String productionOrderNo) {
        if (productionOrderNo == null || productionOrderNo.trim().isEmpty()) {
            return null;
        }
        return prodOrderMapper.selectByOrderNo(productionOrderNo);
    }
}