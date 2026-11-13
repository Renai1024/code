package com.miracle.extra.service.impl;

import com.miracle.extra.domain.MesZhongCeLaoHuaRecordReport;
import com.miracle.extra.mapper.MesZhongCeLaoHuaRecordReportMapper;
import com.miracle.extra.service.IMesZhongCeLaoHuaRecordReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesZhongCeLaoHuaRecordReportServiceImpl implements IMesZhongCeLaoHuaRecordReportService {

    @Autowired
    private MesZhongCeLaoHuaRecordReportMapper mesZhongCeLaoHuaRecordReportMapper;

    @Override
    public List<MesZhongCeLaoHuaRecordReport> selectMesZhongCeLaoHuaRecordReportList(MesZhongCeLaoHuaRecordReport report) {
        return mesZhongCeLaoHuaRecordReportMapper.selectMesZhongCeLaoHuaRecordReportList(report);
    }
}