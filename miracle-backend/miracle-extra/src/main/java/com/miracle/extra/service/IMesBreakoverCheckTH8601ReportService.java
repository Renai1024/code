package com.miracle.extra.service;

import com.miracle.extra.domain.MesBreakoverCheckTH8601Report;

import java.util.List;

/**
 * 数据导通精密综合测试仪TH8601
 */
public interface IMesBreakoverCheckTH8601ReportService {
    /**
     * 获取数据列表
     * @param report
     * @return
     */
    List<MesBreakoverCheckTH8601Report> selectMesBreakoverCheckTH8601ReportList(MesBreakoverCheckTH8601Report report);
}
