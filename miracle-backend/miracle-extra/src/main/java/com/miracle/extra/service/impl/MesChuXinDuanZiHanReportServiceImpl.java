package com.miracle.extra.service.impl;

import com.miracle.extra.domain.MesChuXinDuanZiHanReport;
import com.miracle.extra.domain.MesChuXinNiuZhuanHanReport;
import com.miracle.extra.mapper.MesChuXinDuanZiHanReportMapper;
import com.miracle.extra.mapper.MesChuXinNiuZhuanHanReportMapper;
import com.miracle.extra.service.IMesChuXinDuanZiHanReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 楚鑫端子焊接机数据
 */
@Service
public class MesChuXinDuanZiHanReportServiceImpl implements IMesChuXinDuanZiHanReportService {
    @Autowired
    private MesChuXinDuanZiHanReportMapper mapper;
    @Autowired
    private MesChuXinNiuZhuanHanReportMapper niuzhuanMapper;

    @Override
//    public List<MesChuXinDuanZiHanReport> selectMesChuXinDuanZiHanReportList(MesChuXinDuanZiHanReport report) {
//        List<MesChuXinDuanZiHanReport> list = mapper.selectMesChuXinDuanZiHanReportList(report);
//        return list;
//    }
    public List<MesChuXinNiuZhuanHanReport> selectMesChuXinDuanZiHanReportList(MesChuXinNiuZhuanHanReport report) {
        List<MesChuXinNiuZhuanHanReport> list = niuzhuanMapper.selectMesChuXinDuanZiHanReportList(report);
        return list;
    }
}