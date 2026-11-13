package com.miracle.extra.service;

import com.miracle.extra.domain.MesChuXinNiuZhuanHanReport;

import java.util.List;

/**
 * 楚鑫扭转焊数据
 */
public interface IMesChuXinNiuZhuanHanReportService {
    /**
     * 获取数据列表
     * @param report
     * @return
     */
    List<MesChuXinNiuZhuanHanReport> selectMesChuXinNiuZhuanHanReportList(MesChuXinNiuZhuanHanReport report);

    /**
     * 获取端子焊接数据列表
     * @param report
     * @return
     */
    List<MesChuXinNiuZhuanHanReport> selectMesChuXinDuanZiHanReportList(MesChuXinNiuZhuanHanReport report);

    /**
     * 获取超声波焊接数据列表
     * @param report
     * @return
     */
    List<MesChuXinNiuZhuanHanReport> selectMesChuXinChaoShengBoHanReportList(MesChuXinNiuZhuanHanReport report);
}