package com.miracle.extra.mapper;


import com.miracle.extra.domain.ProdOrderlist;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 生产工单 Mapper 接口
 */
@Mapper
public interface ProdOrderlistMapper {

    /**
     * 根据工单号查询（不包含创建时间和图片）
     */
    ProdOrderlist selectByOrderNo(@Param("productionOrderNo") String productionOrderNo);

    /**
     * 分页列表查询
     */
    List<ProdOrderlist> selectList(@Param("productionOrderNo") String productionOrderNo,
                               @Param("materialCode") String materialCode,
                               @Param("materialName") String materialName);

    /**
     * 统计总数
     */
    Long count(@Param("productionOrderNo") String productionOrderNo,
               @Param("materialCode") String materialCode,
               @Param("materialName") String materialName);

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
    int delete(@Param("productionOrderNo") String productionOrderNo);

    /**
     * 批量删除
     */
    int deleteBatch(@Param("list") List<String> orderNoList);
}