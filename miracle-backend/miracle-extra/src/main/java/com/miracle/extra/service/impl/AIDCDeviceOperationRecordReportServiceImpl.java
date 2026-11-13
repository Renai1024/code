package com.miracle.extra.service.impl;

import com.miracle.extra.domain.AIDCDeviceOperationRecordReport;
import com.miracle.extra.domain.BYDDeviceOperationRecordReport;
import com.miracle.extra.mapper.AIDCDeviceOperationRecordReportMapper;
import com.miracle.extra.mapper.BYDDeviceOperationRecordReportMapper;
import com.miracle.extra.service.IAIDCDeviceOperationRecordReportService;
import com.miracle.extra.service.IBYDDeviceOperationRecordReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AIDCDeviceOperationRecordReportServiceImpl implements IAIDCDeviceOperationRecordReportService {

    @Autowired
    private AIDCDeviceOperationRecordReportMapper aidcDeviceOperationRecordReportMapper;

    @Override
    public List<AIDCDeviceOperationRecordReport> selectAIDCDeviceOperationRecordReportList(AIDCDeviceOperationRecordReport report) {
        List<AIDCDeviceOperationRecordReport> list = aidcDeviceOperationRecordReportMapper.selectAIDCDeviceOperationRecordReportList(report);
        
        for (AIDCDeviceOperationRecordReport record : list) {
            String dataTrackId = record.getDataTrackId();
            
            record.setBarCodeList(aidcDeviceOperationRecordReportMapper.selectBarCodeListByDataTrackId(dataTrackId));
            record.setMaterialBatchList(aidcDeviceOperationRecordReportMapper.selectMaterialBatchListByDataTrackId(dataTrackId));
            record.setMaterialBarCodeList(aidcDeviceOperationRecordReportMapper.selectMaterialBarCodeListByDataTrackId(dataTrackId));
            record.setDataInfoList(aidcDeviceOperationRecordReportMapper.selectDataInfoListByDataTrackId(dataTrackId));
            
            record.setBarCodeListStr(formatBarCodeList(record.getBarCodeList()));
            record.setMaterialBatchListStr(formatMaterialBatchList(record.getMaterialBatchList()));
            record.setMaterialBarCodeListStr(formatMaterialBarCodeList(record.getMaterialBarCodeList()));
            record.setDataInfoListStr(formatDataInfoList(record.getDataInfoList()));
        }
        
        return list;
    }

    private String formatBarCodeList(List<AIDCDeviceOperationRecordReport.OperationBarCodeDetail> list) {
        if (list == null || list.isEmpty()) {
            return "-";
        }
        return list.stream()
                .map(AIDCDeviceOperationRecordReport.OperationBarCodeDetail::getBarCode)
                .collect(Collectors.joining("; "));
    }

    private String formatMaterialBatchList(List<AIDCDeviceOperationRecordReport.OperationMaterialBatchDetail> list) {
        if (list == null || list.isEmpty()) {
            return "-";
        }
        return list.stream()
                .map(item -> item.getMaterialModel() + ":" + item.getMaterialBatch())
                .collect(Collectors.joining("; "));
    }

    private String formatMaterialBarCodeList(List<AIDCDeviceOperationRecordReport.OperationMaterialBarCodeDetail> list) {
        if (list == null || list.isEmpty()) {
            return "-";
        }
        return list.stream()
                .map(AIDCDeviceOperationRecordReport.OperationMaterialBarCodeDetail::getMaterialBarCode)
                .collect(Collectors.joining("; "));
    }

    private String formatDataInfoList(List<AIDCDeviceOperationRecordReport.OperationDataInfoDetail> list) {
        if (list == null || list.isEmpty()) {
            return "-";
        }
        return list.stream()
                .map(item -> item.getDataName() + ":" + item.getDataValue())
                .collect(Collectors.joining("; "));
    }
}