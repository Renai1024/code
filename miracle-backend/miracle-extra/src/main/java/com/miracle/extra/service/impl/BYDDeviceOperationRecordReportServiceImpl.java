package com.miracle.extra.service.impl;

import com.miracle.extra.domain.BYDDeviceOperationRecordReport;
import com.miracle.extra.mapper.BYDDeviceOperationRecordReportMapper;
import com.miracle.extra.service.IBYDDeviceOperationRecordReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BYDDeviceOperationRecordReportServiceImpl implements IBYDDeviceOperationRecordReportService {

    @Autowired
    private BYDDeviceOperationRecordReportMapper bydDeviceOperationRecordReportMapper;

    @Override
    public List<BYDDeviceOperationRecordReport> selectBYDDeviceOperationRecordReportList(BYDDeviceOperationRecordReport report) {
        List<BYDDeviceOperationRecordReport> list = bydDeviceOperationRecordReportMapper.selectBYDDeviceOperationRecordReportList(report);
        
        for (BYDDeviceOperationRecordReport record : list) {
            String dataTrackId = record.getDataTrackId();
            
            record.setBarCodeList(bydDeviceOperationRecordReportMapper.selectBarCodeListByDataTrackId(dataTrackId));
            record.setMaterialBatchList(bydDeviceOperationRecordReportMapper.selectMaterialBatchListByDataTrackId(dataTrackId));
            record.setMaterialBarCodeList(bydDeviceOperationRecordReportMapper.selectMaterialBarCodeListByDataTrackId(dataTrackId));
            record.setDataInfoList(bydDeviceOperationRecordReportMapper.selectDataInfoListByDataTrackId(dataTrackId));
            
            record.setBarCodeListStr(formatBarCodeList(record.getBarCodeList()));
            record.setMaterialBatchListStr(formatMaterialBatchList(record.getMaterialBatchList()));
            record.setMaterialBarCodeListStr(formatMaterialBarCodeList(record.getMaterialBarCodeList()));
            record.setDataInfoListStr(formatDataInfoList(record.getDataInfoList()));
        }
        
        return list;
    }

    private String formatBarCodeList(List<BYDDeviceOperationRecordReport.OperationBarCodeDetail> list) {
        if (list == null || list.isEmpty()) {
            return "-";
        }
        return list.stream()
                .map(BYDDeviceOperationRecordReport.OperationBarCodeDetail::getBarCode)
                .collect(Collectors.joining("; "));
    }

    private String formatMaterialBatchList(List<BYDDeviceOperationRecordReport.OperationMaterialBatchDetail> list) {
        if (list == null || list.isEmpty()) {
            return "-";
        }
        return list.stream()
                .map(item -> item.getMaterialModel() + ":" + item.getMaterialBatch())
                .collect(Collectors.joining("; "));
    }

    private String formatMaterialBarCodeList(List<BYDDeviceOperationRecordReport.OperationMaterialBarCodeDetail> list) {
        if (list == null || list.isEmpty()) {
            return "-";
        }
        return list.stream()
                .map(BYDDeviceOperationRecordReport.OperationMaterialBarCodeDetail::getMaterialBarCode)
                .collect(Collectors.joining("; "));
    }

    private String formatDataInfoList(List<BYDDeviceOperationRecordReport.OperationDataInfoDetail> list) {
        if (list == null || list.isEmpty()) {
            return "-";
        }
        return list.stream()
                .map(item -> item.getDataName() + ":" + item.getDataValue())
                .collect(Collectors.joining("; "));
    }
}