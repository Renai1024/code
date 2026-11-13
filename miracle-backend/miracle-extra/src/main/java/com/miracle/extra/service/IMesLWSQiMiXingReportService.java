package com.miracle.extra.service;

import com.miracle.extra.domain.MesLWSQiMiXingReport;

import java.util.List;

/**
 * 立维斯气密性测试报表
 */
public interface IMesLWSQiMiXingReportService {

    /**
     * 查询立维斯气密性测试数据
     * @param mesLWSQiMiXingReport
     * @return
     */
    public List<MesLWSQiMiXingReport> selectMesLWSQiMiXingReportList(MesLWSQiMiXingReport mesLWSQiMiXingReport);
}
