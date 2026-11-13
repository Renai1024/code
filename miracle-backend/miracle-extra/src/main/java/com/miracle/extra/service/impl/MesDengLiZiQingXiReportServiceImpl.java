package com.miracle.extra.service.impl;

import com.miracle.extra.domain.MesDengLiZiQingXiReport;
import com.miracle.extra.mapper.MesDengLiZiQingXiReportMapper;
import com.miracle.extra.service.IMesDengLiZiQingXiReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesDengLiZiQingXiReportServiceImpl implements IMesDengLiZiQingXiReportService {

    @Autowired
    private MesDengLiZiQingXiReportMapper mesDengLiZiQingXiReportMapper;

    @Override
    public List<MesDengLiZiQingXiReport> selectMesDengLiZiQingXiReportList(MesDengLiZiQingXiReport report) {
        return mesDengLiZiQingXiReportMapper.selectMesDengLiZiQingXiReportList(report);
    }
}