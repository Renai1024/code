package com.miracle.extra.service.impl;

import com.miracle.extra.domain.MesLWSQiMiXingReport;
import com.miracle.extra.mapper.MesLWSQiMiXingReportMapper;
import com.miracle.extra.service.IMesLWSQiMiXingReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 立维斯耐压测试报表服务实现类
 */
@Service
public class MesLWSQiMiXingReportServiceImpl implements IMesLWSQiMiXingReportService {

    @Autowired
    private MesLWSQiMiXingReportMapper mesLWSQiMiXingReportMapper;

    @Override
    public List<MesLWSQiMiXingReport> selectMesLWSQiMiXingReportList(MesLWSQiMiXingReport mesLWSQiMiXingReport) {
        return mesLWSQiMiXingReportMapper.selectMesLWSQiMiXingReportList(mesLWSQiMiXingReport);
    }
}
