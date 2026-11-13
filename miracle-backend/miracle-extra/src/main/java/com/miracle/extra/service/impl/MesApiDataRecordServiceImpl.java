package com.miracle.extra.service.impl;

import com.miracle.extra.domain.MesApiDataRecord;
import com.miracle.extra.mapper.MesApiDataRecordMapper;
import com.miracle.extra.service.IMesApiDataRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MesApiDataRecordServiceImpl implements IMesApiDataRecordService {

    @Autowired
    private MesApiDataRecordMapper mesApiDataRecordMapper;

    @Override
    public List<MesApiDataRecord> selectMesApiDataRecordList(MesApiDataRecord record) {
        List<MesApiDataRecord> list = mesApiDataRecordMapper.selectMesApiDataRecordList(record);
        
        for (MesApiDataRecord item : list) {
            String dataTrackId = item.getDataTrackId();
            
            item.setBarCodeList(mesApiDataRecordMapper.selectBarCodeListByDataTrackId(dataTrackId));
            item.setMaterialBatchList(mesApiDataRecordMapper.selectMaterialBatchListByDataTrackId(dataTrackId));
            item.setMaterialBarCodeList(mesApiDataRecordMapper.selectMaterialBarCodeListByDataTrackId(dataTrackId));
            item.setDataInfoList(mesApiDataRecordMapper.selectDataInfoListByDataTrackId(dataTrackId));
            
            item.setBarCodeListStr(formatBarCodeList(item.getBarCodeList()));
            item.setMaterialBatchListStr(formatMaterialBatchList(item.getMaterialBatchList()));
            item.setMaterialBarCodeListStr(formatMaterialBarCodeList(item.getMaterialBarCodeList()));
            item.setDataInfoListStr(formatDataInfoList(item.getDataInfoList()));
        }
        
        return list;
    }

    private String formatBarCodeList(List<MesApiDataRecord.MesApiDataBarCodeDetail> list) {
        if (list == null || list.isEmpty()) {
            return "-";
        }
        return list.stream()
                .map(MesApiDataRecord.MesApiDataBarCodeDetail::getBarCode)
                .collect(Collectors.joining("; "));
    }

    private String formatMaterialBatchList(List<MesApiDataRecord.MesApiDataMaterialBatchDetail> list) {
        if (list == null || list.isEmpty()) {
            return "-";
        }
        return list.stream()
                .map(item -> item.getMaterialModel() + ":" + item.getMaterialBatch())
                .collect(Collectors.joining("; "));
    }

    private String formatMaterialBarCodeList(List<MesApiDataRecord.MesApiDataMaterialBarCodeDetail> list) {
        if (list == null || list.isEmpty()) {
            return "-";
        }
        return list.stream()
                .map(MesApiDataRecord.MesApiDataMaterialBarCodeDetail::getMaterialBarCode)
                .collect(Collectors.joining("; "));
    }

    private String formatDataInfoList(List<MesApiDataRecord.MesApiDataInfoDetail> list) {
        if (list == null || list.isEmpty()) {
            return "-";
        }
        return list.stream()
                .map(item -> item.getDataName() + ":" + item.getDataValue())
                .collect(Collectors.joining("; "));
    }
}