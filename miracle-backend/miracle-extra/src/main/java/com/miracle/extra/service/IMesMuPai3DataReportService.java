package com.miracle.extra.service;

import com.miracle.extra.domain.MesMuPai3DataReport;

import java.util.List;

public interface IMesMuPai3DataReportService {

    /**
     * 查询海思耐压测试数据列表
     * @param report
     * @return
     */
    List<MesMuPai3DataReport> selectHSnycsDataReportList(MesMuPai3DataReport report);
}
