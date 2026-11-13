package com.miracle.extra.service;

import com.miracle.extra.domain.MesApiDataRecord;

import java.util.List;

public interface IMesApiDataRecordService {
    List<MesApiDataRecord> selectMesApiDataRecordList(MesApiDataRecord record);
}