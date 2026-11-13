package com.miracle.extra.service.impl;

import com.miracle.extra.domain.MesBreakoverCheckTH8601Report;
import com.miracle.extra.mapper.MesBreakoverCheckTH8601ReportMapper;
import com.miracle.extra.service.IMesBreakoverCheckTH8601ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 数据导通精密综合测试仪TH8601
 */
@Service
public class MesBreakoverCheckTH8601ReportServiceImpl implements IMesBreakoverCheckTH8601ReportService {
    @Autowired
    private MesBreakoverCheckTH8601ReportMapper mapper;

    @Override
    public List<MesBreakoverCheckTH8601Report> selectMesBreakoverCheckTH8601ReportList(MesBreakoverCheckTH8601Report report) {
        List<MesBreakoverCheckTH8601Report> list = mapper.selectMesBreakoverCheckTH8601ReportList(report);
        return list;
    }
}
