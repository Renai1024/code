package com.miracle.extra.service.impl;

import com.github.pagehelper.PageHelper;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.extra.domain.MesSheBeiZuDataReport;
import com.miracle.extra.mapper.MesSheBeiZuDataReportMapper;
import com.miracle.extra.service.IMesSheBeiZuDataReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesSheBeiZuDataReportServiceImpl implements IMesSheBeiZuDataReportService {

    @Autowired
    private MesSheBeiZuDataReportMapper mapper;

    @Override
    public List<MesSheBeiZuDataReport> selectMesSheBeiZuDataReportList(MesSheBeiZuDataReport report) {
        List<MesSheBeiZuDataReport> list = mapper.selectMesSheBeiZuDataReportList(report);
        return list;
    }
}
