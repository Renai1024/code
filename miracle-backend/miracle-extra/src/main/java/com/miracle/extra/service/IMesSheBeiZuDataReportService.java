package com.miracle.extra.service;

import com.miracle.extra.domain.MesSheBeiZuDataReport;

import java.util.List;

public interface IMesSheBeiZuDataReportService {

    /**
     *  获取线材导通测试数据列表
     * @param report
     * @return
     */
    List<MesSheBeiZuDataReport> selectMesSheBeiZuDataReportList(MesSheBeiZuDataReport report);
}
