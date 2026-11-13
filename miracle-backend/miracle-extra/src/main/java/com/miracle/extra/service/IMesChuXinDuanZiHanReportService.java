package com.miracle.extra.service;

import com.miracle.extra.domain.MesChuXinDuanZiHanReport;
import com.miracle.extra.domain.MesChuXinNiuZhuanHanReport;

import java.util.List;

/**
 * 楚鑫端子焊接机数据
 */
public interface IMesChuXinDuanZiHanReportService {
    /**
     * 获取数据列表
     * @param report
     * @return
     */
    //List<MesChuXinDuanZiHanReport> selectMesChuXinDuanZiHanReportList(MesChuXinDuanZiHanReport report);
    List<MesChuXinNiuZhuanHanReport> selectMesChuXinDuanZiHanReportList(MesChuXinNiuZhuanHanReport report);

}