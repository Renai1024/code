package com.miracle.extra.service.impl;

import com.miracle.extra.domain.MesDianCeXianReport;
import com.miracle.extra.mapper.MesDianCeXianReportMapper;
import com.miracle.extra.service.IMesDianCeXianReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 电测线报表服务实现类
 */
@Service
public class MesDianCeXianReportServiceImpl implements IMesDianCeXianReportService {

    @Autowired
    private MesDianCeXianReportMapper mesMesDianCeXianReportMapper;

    @Override
    public List<MesDianCeXianReport> selectMesDianCeXianReportList(MesDianCeXianReport mesDianCeXianReport) {
        return mesMesDianCeXianReportMapper.selectMesDianCeXianReportList(mesDianCeXianReport);
    }
}
