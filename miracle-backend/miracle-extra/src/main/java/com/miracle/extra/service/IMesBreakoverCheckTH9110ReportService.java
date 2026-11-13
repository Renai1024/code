package com.miracle.extra.service;

import com.miracle.extra.domain.MesBreakoverCheckTH9110Report;

import java.util.List;

/**
 * 绝缘电阻测试仪TH9110
 */
public interface IMesBreakoverCheckTH9110ReportService {

    /**
     * 获取数据列表
     * @param report
     * @return
     */
    List<MesBreakoverCheckTH9110Report> selectMesBreakoverCheckTH9110ReportList(MesBreakoverCheckTH9110Report report);
}
