package com.miracle.extra.service;

import com.miracle.extra.domain.ProdOrderlist;

import java.util.List;

/**
 * 生产工单 Service 接口
 */
public interface ProdOrderlistService {

    /**
     * 根据工单号查询
     */
    ProdOrderlist getByOrderNo(String productionOrderNo);

    /**
     * 分页列表查询
     */
    List<ProdOrderlist> list(String productionOrderNo, String materialCode, String materialName);

    /**
     * 统计总数
     */
    Long count(String productionOrderNo, String materialCode, String materialName);

    /**
     * 新增
     */
    int insert(ProdOrderlist prodOrder);

    /**
     * 批量新增
     */
    int insertBatch(List<ProdOrderlist> list);

    /**
     * 修改
     */
    int update(ProdOrderlist prodOrder);

    /**
     * 删除
     */
    int delete(String productionOrderNo);

    /**
     * 批量删除
     */
    int deleteBatch(List<String> orderNoList);
}