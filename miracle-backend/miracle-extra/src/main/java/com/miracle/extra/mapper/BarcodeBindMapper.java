package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.BarcodeBindRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@DataSource(value = DataSourceType.SLAVE6)
@Mapper
public interface BarcodeBindMapper {
    
    int insertBarcodeBindRecord(BarcodeBindRecord record);
    
    BarcodeBindRecord selectByBindUuid(@Param("bindUuid") String bindUuid);
    
    List<BarcodeBindRecord> selectBarcodeBindRecordList(BarcodeBindRecord record);
    
    List<BarcodeBindRecord> selectByBarcode(@Param("barcodeType") String barcodeType, @Param("barcode") String barcode);
    
    int deleteBarcodeBindRecordById(@Param("id") Long id);
    
    int updateBarcodeBindRecord(BarcodeBindRecord record);
}