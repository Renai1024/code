package com.miracle.extra.service;

import com.miracle.extra.domain.MesDianCeXianReport;

import java.util.List;

/**
 * 电测线报表
 */
public interface IMesDianCeXianReportService {

    /**
     * 查询电测线数据
     * @param mesDianCeXianReport
     * @return
     */
    public List<MesDianCeXianReport> selectMesDianCeXianReportList(MesDianCeXianReport mesDianCeXianReport);
}
