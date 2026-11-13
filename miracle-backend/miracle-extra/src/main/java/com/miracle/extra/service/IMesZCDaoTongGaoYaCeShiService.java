package com.miracle.extra.service;

import com.miracle.extra.domain.MesLWSQiMiXingReport;
import com.miracle.extra.domain.MesZCDaoTongGaoYaCeShi;

import java.util.List;

/**
 * 中测导通高压测试报表
 */
public interface IMesZCDaoTongGaoYaCeShiService {

    /**
     * 查询中测导通高压测试数据
     * @param mesZCDaoTongGaoYaCeShi
     * @return
     */
    public List<MesZCDaoTongGaoYaCeShi> selectMesZCDaoTongGaoYaCeShiList(MesZCDaoTongGaoYaCeShi mesZCDaoTongGaoYaCeShi);
}
