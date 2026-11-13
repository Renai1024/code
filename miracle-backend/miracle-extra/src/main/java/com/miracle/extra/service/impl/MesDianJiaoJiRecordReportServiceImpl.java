package com.miracle.extra.service.impl;

import com.miracle.extra.domain.MesDianJiaoJiRecordReport;
import com.miracle.extra.mapper.MesDianJiaoJiRecordReportMapper;
import com.miracle.extra.service.IMesDianJiaoJiRecordReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 典帧端子压接机报表服务实现类
 */
@Service
public class MesDianJiaoJiRecordReportServiceImpl implements IMesDianJiaoJiRecordReportService {

    @Autowired
    private MesDianJiaoJiRecordReportMapper MesDianJiaoJiRecordReportMapper;

    @Override
    public List<MesDianJiaoJiRecordReport> selectMesDZDuanziyajiejdList(MesDianJiaoJiRecordReport mesDianJiaoJiRecordReport) {
        return MesDianJiaoJiRecordReportMapper.selectMesDianJiaoJiRecordReportList(mesDianJiaoJiRecordReport);
    }
}
