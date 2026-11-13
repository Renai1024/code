package com.miracle.extra.service.impl;

import com.miracle.extra.domain.MesZhongCeChargeModeReport;
import com.miracle.extra.mapper.MesZhongCeChargeModeReportMapper;
import com.miracle.extra.service.IMesZhongCeChargeModeReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesZhongCeChargeModeReportServiceImpl implements IMesZhongCeChargeModeReportService {

    @Autowired
    private MesZhongCeChargeModeReportMapper mesZhongCeChargeModeReportMapper;

    @Override
    public List<MesZhongCeChargeModeReport> selectMesZhongCeChargeModeReportList(MesZhongCeChargeModeReport report) {
        return mesZhongCeChargeModeReportMapper.selectMesZhongCeChargeModeReportList(report);
    }
}