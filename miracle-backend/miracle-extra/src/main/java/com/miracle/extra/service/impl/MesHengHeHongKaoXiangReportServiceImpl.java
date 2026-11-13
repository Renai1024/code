package com.miracle.extra.service.impl;

import com.miracle.extra.domain.MesHengHeHongKaoXiangReport;
import com.miracle.extra.mapper.MesHengHeHongKaoXiangReportMapper;
import com.miracle.extra.service.IMesHengHeHongKaoXiangReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesHengHeHongKaoXiangReportServiceImpl implements IMesHengHeHongKaoXiangReportService {

    @Autowired
    private MesHengHeHongKaoXiangReportMapper mesHengHeHongKaoXiangReportMapper;

    @Override
    public List<MesHengHeHongKaoXiangReport> selectMesHengHeHongKaoXiangReportList(MesHengHeHongKaoXiangReport report) {
        return mesHengHeHongKaoXiangReportMapper.selectMesHengHeHongKaoXiangReportList(report);
    }
}