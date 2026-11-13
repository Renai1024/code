package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.MesLWSQiMiXingReport;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 立维斯气密性测试报表Mapper
 */
@DataSource(value = DataSourceType.SLAVE5)
@Mapper
public interface MesLWSQiMiXingReportMapper {

    /**
     * 查询立维斯气密性测试结果列表
     * @param mesLWSQiMiXingReport
     * @return
     */
    public List<MesLWSQiMiXingReport> selectMesLWSQiMiXingReportList(MesLWSQiMiXingReport mesLWSQiMiXingReport);
}
