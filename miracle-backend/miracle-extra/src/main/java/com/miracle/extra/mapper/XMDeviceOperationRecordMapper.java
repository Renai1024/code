package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.XMDeviceOperationRecord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@DataSource(value = DataSourceType.SLAVE5)
@Mapper
public interface XMDeviceOperationRecordMapper {
    List<XMDeviceOperationRecord> selectXMDeviceOperationRecordList(XMDeviceOperationRecord record);
    
    List<XMDeviceOperationRecord.XMOperationBarCodeDetail> selectBarCodeListByDataTrackId(String dataTrackId);
    
    List<XMDeviceOperationRecord.XMOperationMaterialBatchDetail> selectMaterialBatchListByDataTrackId(String dataTrackId);
    
    List<XMDeviceOperationRecord.XMOperationMaterialBarCodeDetail> selectMaterialBarCodeListByDataTrackId(String dataTrackId);
    
    List<XMDeviceOperationRecord.XMOperationDataInfoDetail> selectDataInfoListByDataTrackId(String dataTrackId);
}