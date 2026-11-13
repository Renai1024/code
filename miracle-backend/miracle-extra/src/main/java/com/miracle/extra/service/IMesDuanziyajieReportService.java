package com.miracle.extra.service;

import com.miracle.extra.domain.MesDuanziyajieReport;

import java.util.List;

/**
 * 端子压接报表
 */
public interface IMesDuanziyajieReportService {

    /**
     * 查询端子压接数据
     * @param mesDuanziyajieReport
     * @return
     */
    public List<MesDuanziyajieReport> selectMesDuanziyajieReportList(MesDuanziyajieReport mesDuanziyajieReport);
}
