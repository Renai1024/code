package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.AIDCDeviceOperationRecordReport;
import com.miracle.extra.domain.BYDDeviceOperationRecordReport;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@DataSource(value = DataSourceType.SLAVE5)
@Mapper
public interface AIDCDeviceOperationRecordReportMapper {
    List<AIDCDeviceOperationRecordReport> selectAIDCDeviceOperationRecordReportList(AIDCDeviceOperationRecordReport report);
    
    List<AIDCDeviceOperationRecordReport.OperationBarCodeDetail> selectBarCodeListByDataTrackId(String dataTrackId);
    
    List<AIDCDeviceOperationRecordReport.OperationMaterialBatchDetail> selectMaterialBatchListByDataTrackId(String dataTrackId);
    
    List<AIDCDeviceOperationRecordReport.OperationMaterialBarCodeDetail> selectMaterialBarCodeListByDataTrackId(String dataTrackId);
    
    List<AIDCDeviceOperationRecordReport.OperationDataInfoDetail> selectDataInfoListByDataTrackId(String dataTrackId);
}