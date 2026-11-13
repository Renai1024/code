package com.miracle.extra.service;

import com.miracle.extra.domain.MesDianJiaoJiRecordReport;

import java.util.List;

/**
 * 点胶机报表
 */
public interface IMesDianJiaoJiRecordReportService {

    /**
     * 查询点胶机数据
     * @param mesDianJiaoJiRecordReport
     * @return
     */
    public List<MesDianJiaoJiRecordReport> selectMesDZDuanziyajiejdList(MesDianJiaoJiRecordReport mesDianJiaoJiRecordReport);
}
