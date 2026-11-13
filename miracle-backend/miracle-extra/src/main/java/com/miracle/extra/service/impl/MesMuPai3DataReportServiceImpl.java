package com.miracle.extra.service.impl;

import com.miracle.extra.domain.MesMuPai3DataReport;
import com.miracle.extra.mapper.MesMuPai3DataReportMapper;
import com.miracle.extra.service.IMesMuPai3DataReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesMuPai3DataReportServiceImpl implements IMesMuPai3DataReportService {

    @Autowired
    private MesMuPai3DataReportMapper mapper;

    @Override
    public List<MesMuPai3DataReport> selectHSnycsDataReportList(MesMuPai3DataReport report) {
        report.setCustomerFlag("HS");
        List<MesMuPai3DataReport> list = mapper.selectMesMuPai3DataReportList(report);
        return list;
    }
}
