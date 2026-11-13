package com.miracle.extra.service.impl;

import com.miracle.extra.domain.XMDeviceOperationRecord;
import com.miracle.extra.mapper.XMDeviceOperationRecordMapper;
import com.miracle.extra.service.IXMDeviceOperationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class XMDeviceOperationRecordServiceImpl implements IXMDeviceOperationRecordService {

    @Autowired
    private XMDeviceOperationRecordMapper xmDeviceOperationRecordMapper;

    @Override
    public List<XMDeviceOperationRecord> selectXMDeviceOperationRecordList(XMDeviceOperationRecord record) {
        List<XMDeviceOperationRecord> list = xmDeviceOperationRecordMapper.selectXMDeviceOperationRecordList(record);
        
        for (XMDeviceOperationRecord item : list) {
            String dataTrackId = item.getDataTrackId();
            
            item.setBarCodeList(xmDeviceOperationRecordMapper.selectBarCodeListByDataTrackId(dataTrackId));
            item.setMaterialBatchList(xmDeviceOperationRecordMapper.selectMaterialBatchListByDataTrackId(dataTrackId));
            item.setMaterialBarCodeList(xmDeviceOperationRecordMapper.selectMaterialBarCodeListByDataTrackId(dataTrackId));
            item.setDataInfoList(xmDeviceOperationRecordMapper.selectDataInfoListByDataTrackId(dataTrackId));
            
            item.setBarCodeListStr(formatBarCodeList(item.getBarCodeList()));
            item.setMaterialBatchListStr(formatMaterialBatchList(item.getMaterialBatchList()));
            item.setMaterialBarCodeListStr(formatMaterialBarCodeList(item.getMaterialBarCodeList()));
            item.setDataInfoListStr(formatDataInfoList(item.getDataInfoList()));
        }
        
        return list;
    }

    private String formatBarCodeList(List<XMDeviceOperationRecord.XMOperationBarCodeDetail> list) {
        if (list == null || list.isEmpty()) {
            return "-";
        }
        return list.stream()
                .map(XMDeviceOperationRecord.XMOperationBarCodeDetail::getBarCode)
                .collect(Collectors.joining("; "));
    }

    private String formatMaterialBatchList(List<XMDeviceOperationRecord.XMOperationMaterialBatchDetail> list) {
        if (list == null || list.isEmpty()) {
            return "-";
        }
        return list.stream()
                .map(item -> item.getMaterialModel() + ":" + item.getMaterialBatch())
                .collect(Collectors.joining("; "));
    }

    private String formatMaterialBarCodeList(List<XMDeviceOperationRecord.XMOperationMaterialBarCodeDetail> list) {
        if (list == null || list.isEmpty()) {
            return "-";
        }
        return list.stream()
                .map(XMDeviceOperationRecord.XMOperationMaterialBarCodeDetail::getMaterialBarCode)
                .collect(Collectors.joining("; "));
    }

    private String formatDataInfoList(List<XMDeviceOperationRecord.XMOperationDataInfoDetail> list) {
        if (list == null || list.isEmpty()) {
            return "-";
        }
        return list.stream()
                .map(item -> item.getDataName() + ":" + item.getDataValue())
                .collect(Collectors.joining("; "));
    }
}