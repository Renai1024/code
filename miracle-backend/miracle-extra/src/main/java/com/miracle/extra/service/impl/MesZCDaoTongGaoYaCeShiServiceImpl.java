package com.miracle.extra.service.impl;

import com.miracle.extra.domain.MesLWSQiMiXingReport;
import com.miracle.extra.domain.MesZCDaoTongGaoYaCeShi;
import com.miracle.extra.mapper.MesLWSQiMiXingReportMapper;
import com.miracle.extra.mapper.MesZCDaoTongGaoYaCeShiMapper;
import com.miracle.extra.service.IMesLWSQiMiXingReportService;
import com.miracle.extra.service.IMesZCDaoTongGaoYaCeShiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 中测导通高压测试服务实现类
 */
@Service
public class MesZCDaoTongGaoYaCeShiServiceImpl implements IMesZCDaoTongGaoYaCeShiService {

    @Autowired
    private MesZCDaoTongGaoYaCeShiMapper mesZCDaoTongGaoYaCeShiMapper;

    @Override
    public List<MesZCDaoTongGaoYaCeShi> selectMesZCDaoTongGaoYaCeShiList(MesZCDaoTongGaoYaCeShi mesZCDaoTongGaoYaCeShi) {
        return mesZCDaoTongGaoYaCeShiMapper.selectMesZCDaoTongGaoYaCeShiList(mesZCDaoTongGaoYaCeShi);
    }
}
