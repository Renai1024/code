package com.miracle.extra.service.impl;

import com.miracle.extra.domain.MesDianCeXianReport;
import com.miracle.extra.domain.MesTSDZhiNengDianPiReport;
import com.miracle.extra.mapper.MesDianCeXianReportMapper;
import com.miracle.extra.mapper.MesTSDZhiNengDianPiReportMapper;
import com.miracle.extra.service.IMesDianCeXianReportService;
import com.miracle.extra.service.IMesTSDZhiNengDianPiReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * TSD智能电批报表服务实现类
 */
@Service
public class MesTSDZhiNengDianPiReportServiceImpl implements IMesTSDZhiNengDianPiReportService {

    @Autowired
    private MesTSDZhiNengDianPiReportMapper mesTSDZhiNengDianPiReportMapper;

    @Override
    public List<MesTSDZhiNengDianPiReport> selectMesTSDZhiNengDianPiReportList(MesTSDZhiNengDianPiReport mesTSDZhiNengDianPiReport) {
        return mesTSDZhiNengDianPiReportMapper.selectMesTSDZhiNengDianPiReportList(mesTSDZhiNengDianPiReport);
    }
}
