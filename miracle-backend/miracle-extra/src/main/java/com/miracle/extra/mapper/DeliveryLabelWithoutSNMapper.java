package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.DeliveryLabelWithoutSN;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 出货标签（无SN）Mapper接口
 * 数据源：slave4 (SancoERP.dbo.vwOutBoxLabelRecord 视图)
 * 该视图已过滤 CustomerFlag = 'BYDOutBox'
 *
 * @author miracle
 */
@DataSource(value = DataSourceType.SLAVE4)
@Mapper
public interface DeliveryLabelWithoutSNMapper {

    /**
     * 根据条码查询出货标签
     *
     * @param barCodeStr 条码
     * @return 出货标签信息
     */
    DeliveryLabelWithoutSN selectByBarCodeStr(@Param("barCodeStr") String barCodeStr);

    /**
     * 根据客户标签标识查询列表
     *
     * @param customerFlag 客户标签标识（如BYDOutBox）
     * @return 出货标签列表
     */
    List<DeliveryLabelWithoutSN> selectListByCustomerFlag(@Param("customerFlag") String customerFlag);
}
