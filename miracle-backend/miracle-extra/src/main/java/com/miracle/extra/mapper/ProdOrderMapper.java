package com.miracle.extra.mapper;


import com.miracle.extra.domain.ProdOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 生产工单 Mapper 接口
 */
@Mapper
public interface ProdOrderMapper {

    /**
     * 根据工单号查询工单信息（不包含创建时间和图片）
     * @param productionOrderNo 生产工单号
     * @return 工单信息
     */
    ProdOrder selectByOrderNo(@Param("productionOrderNo") String productionOrderNo);

    /**
     * 根据工单号列表批量查询
     * @param orderNoList 工单号列表
     * @return 工单信息列表
     */
    List<ProdOrder> selectByOrderNoList(@Param("orderNoList") List<String> orderNoList);
}