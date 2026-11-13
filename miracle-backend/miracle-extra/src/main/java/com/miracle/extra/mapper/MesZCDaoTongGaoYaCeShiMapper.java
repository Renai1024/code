package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.MesLWSQiMiXingReport;
import com.miracle.extra.domain.MesZCDaoTongGaoYaCeShi;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 中测导通高压测试报表Mapper
 */
@DataSource(value = DataSourceType.SLAVE5)
@Mapper
public interface MesZCDaoTongGaoYaCeShiMapper {

    /**
     * 查询中测导通高压测试结果列表
     * @param mesZCDaoTongGaoYaCeShi
     * @return
     */
    public List<MesZCDaoTongGaoYaCeShi> selectMesZCDaoTongGaoYaCeShiList(MesZCDaoTongGaoYaCeShi mesZCDaoTongGaoYaCeShi);
}
