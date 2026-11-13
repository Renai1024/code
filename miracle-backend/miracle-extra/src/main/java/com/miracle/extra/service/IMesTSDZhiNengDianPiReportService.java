package com.miracle.extra.service;

import com.miracle.extra.domain.MesDianCeXianReport;
import com.miracle.extra.domain.MesTSDZhiNengDianPiReport;

import java.util.List;

/**
 * TSD智能电批报表
 */
public interface IMesTSDZhiNengDianPiReportService {

    /**
     * 查询TSD智能电批数据
     * @param mesTSDZhiNengDianPiReport
     * @return
     */
    public List<MesTSDZhiNengDianPiReport> selectMesTSDZhiNengDianPiReportList(MesTSDZhiNengDianPiReport mesTSDZhiNengDianPiReport);
}
