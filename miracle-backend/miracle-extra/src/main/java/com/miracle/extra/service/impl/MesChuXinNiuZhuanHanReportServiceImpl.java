package com.miracle.extra.service.impl;

import com.miracle.extra.domain.MesChuXinNiuZhuanHanReport;
import com.miracle.extra.mapper.MesChuXinNiuZhuanHanReportMapper;
import com.miracle.extra.service.IMesChuXinNiuZhuanHanReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 初心扭转焊数据
 */
@Service
public class MesChuXinNiuZhuanHanReportServiceImpl implements IMesChuXinNiuZhuanHanReportService {
    @Autowired
    private MesChuXinNiuZhuanHanReportMapper mapper;

    @Override
    public List<MesChuXinNiuZhuanHanReport> selectMesChuXinNiuZhuanHanReportList(MesChuXinNiuZhuanHanReport report) {
        List<MesChuXinNiuZhuanHanReport> list = mapper.selectMesChuXinNiuZhuanHanReportList(report);
        return list;
    }

    @Override
    public List<MesChuXinNiuZhuanHanReport> selectMesChuXinDuanZiHanReportList(MesChuXinNiuZhuanHanReport report) {
        List<MesChuXinNiuZhuanHanReport> list = mapper.selectMesChuXinDuanZiHanReportList(report);
        return list;
    }

    @Override
    public List<MesChuXinNiuZhuanHanReport> selectMesChuXinChaoShengBoHanReportList(MesChuXinNiuZhuanHanReport report) {
        List<MesChuXinNiuZhuanHanReport> list = mapper.selectMesChuXinChaoShengBoHanReportList(report);
        return list;
    }
}