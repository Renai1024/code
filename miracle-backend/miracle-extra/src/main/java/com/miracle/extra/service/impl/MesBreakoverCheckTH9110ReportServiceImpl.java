package com.miracle.extra.service.impl;

import com.miracle.extra.domain.MesBreakoverCheckTH9110Report;
import com.miracle.extra.mapper.MesBreakoverCheckTH9110ReportMapper;
import com.miracle.extra.service.IMesBreakoverCheckTH9110ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesBreakoverCheckTH9110ReportServiceImpl implements IMesBreakoverCheckTH9110ReportService {

    @Autowired
    private MesBreakoverCheckTH9110ReportMapper mapper;

    @Override
    public List<MesBreakoverCheckTH9110Report> selectMesBreakoverCheckTH9110ReportList(MesBreakoverCheckTH9110Report report) {
        List<MesBreakoverCheckTH9110Report> list = mapper.selectMesBreakoverCheckTH9110ReportList(report);
        return list;
    }
}
