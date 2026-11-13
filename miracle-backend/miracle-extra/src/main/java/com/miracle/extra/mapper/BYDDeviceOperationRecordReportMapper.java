package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.BYDDeviceOperationRecordReport;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@DataSource(value = DataSourceType.SLAVE5)
@Mapper
public interface BYDDeviceOperationRecordReportMapper {
    List<BYDDeviceOperationRecordReport> selectBYDDeviceOperationRecordReportList(BYDDeviceOperationRecordReport report);
    
    List<BYDDeviceOperationRecordReport.OperationBarCodeDetail> selectBarCodeListByDataTrackId(String dataTrackId);
    
    List<BYDDeviceOperationRecordReport.OperationMaterialBatchDetail> selectMaterialBatchListByDataTrackId(String dataTrackId);
    
    List<BYDDeviceOperationRecordReport.OperationMaterialBarCodeDetail> selectMaterialBarCodeListByDataTrackId(String dataTrackId);
    
    List<BYDDeviceOperationRecordReport.OperationDataInfoDetail> selectDataInfoListByDataTrackId(String dataTrackId);
}