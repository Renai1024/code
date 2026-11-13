package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.ProductRoutingStep;
import com.miracle.extra.domain.ProductSN;
import com.miracle.extra.domain.StationRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@DataSource(value = DataSourceType.SLAVE6)
@Mapper
public interface ProcessStationMapper {
    
    Integer selectEquipmentIdByCode(@Param("equipmentCode") String equipmentCode);
    
    Integer selectProductIdByCode(@Param("productCode") String productCode);
    
    Integer selectProductRoutingIdByProductId(@Param("productId") Integer productId);
    
    List<ProductSN> selectProductSNsByBarcode(@Param("barcode") String barcode);
    
    List<ProductRoutingStep> selectDeviceSteps(@Param("routingId") Integer routingId, @Param("equipmentId") Integer equipmentId);
    
    List<ProductRoutingStep> selectAllSteps(@Param("routingId") Integer routingId);
    
    List<StationRecord> selectStationRecordsByBarcode(@Param("barcode") String barcode);
    
    String selectBarcodeRuleByProductId(@Param("productId") Integer productId);
    
    Long insertProductSN(@Param("barcode") String barcode, 
                         @Param("productId") Integer productId, 
                         @Param("routingId") Integer routingId, 
                         @Param("status") Integer status, 
                         @Param("createTime") Date createTime, 
                         @Param("createBy") String createBy);
    
    int updateProductSN(@Param("id") Long id, 
                        @Param("status") Integer status, 
                        @Param("lastProcessId") Integer lastProcessId, 
                        @Param("nextProcessIds") String nextProcessIds);
    
    int insertStationRecord(@Param("barcode") String barcode,
                            @Param("productId") Integer productId,
                            @Param("productRoutingId") Integer productRoutingId,
                            @Param("processId") Integer processId,
                            @Param("equipmentId") Integer equipmentId,
                            @Param("result") Short result,
                            @Param("resultDetail") String resultDetail,
                            @Param("dataTraceId") String dataTraceId,
                            @Param("createTime") Date createTime);
}