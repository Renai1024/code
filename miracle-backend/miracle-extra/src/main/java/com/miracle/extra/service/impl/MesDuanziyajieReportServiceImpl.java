package com.miracle.extra.service.impl;

import com.miracle.extra.domain.MesDuanziyajieReport;
import com.miracle.extra.mapper.MesDuanziyajieReportMapper;
import com.miracle.extra.service.IMesDuanziyajieReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 端子压接机报表服务实现类
 */
@Service
public class MesDuanziyajieReportServiceImpl implements IMesDuanziyajieReportService {

    @Autowired
    private MesDuanziyajieReportMapper mesMesDuanziyajieReportMapper;

    @Override
    public List<MesDuanziyajieReport> selectMesDuanziyajieReportList(MesDuanziyajieReport mesDuanziyajieReport) {
        return mesMesDuanziyajieReportMapper.selectMesDuanziyajieReportList(mesDuanziyajieReport);
    }
}
