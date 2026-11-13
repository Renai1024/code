package com.miracle.extra.controller;


import com.miracle.common.annotation.Anonymous;
import com.miracle.extra.domain.ProdOrder;
import com.miracle.extra.service.ProdOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 生产工单 Controller
 */
@RestController
@Anonymous
@RequestMapping("/api/prod-order")
public class ProdOrderController {

    @Autowired
    private ProdOrderService prodOrderService;

    /**
     * 根据工单号查询工单信息（不包含创建时间和图片）
     * @param productionOrderNo 生产工单号
     * @return 工单信息
     */
    @GetMapping("/{productionOrderNo}")
    public ProdOrder getByOrderNo(@PathVariable("productionOrderNo") String productionOrderNo) {
        return prodOrderService.getByOrderNo(productionOrderNo);
    }
}