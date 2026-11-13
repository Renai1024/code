package com.miracle.extra.service.impl;

import com.miracle.extra.domain.MesLiErLiChaBaLiReport;
import com.miracle.extra.mapper.MesLiErLiChaBaLiReportMapper;
import com.miracle.extra.service.IMesLiErLiChaBaLiReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesLiErLiChaBaLiReportServiceImpl implements IMesLiErLiChaBaLiReportService {

    @Autowired
    private MesLiErLiChaBaLiReportMapper mesLiErLiChaBaLiReportMapper;

    @Override
    public List<MesLiErLiChaBaLiReport> selectMesLiErLiChaBaLiReportList(MesLiErLiChaBaLiReport report) {
        return mesLiErLiChaBaLiReportMapper.selectMesLiErLiChaBaLiReportList(report);
    }
}