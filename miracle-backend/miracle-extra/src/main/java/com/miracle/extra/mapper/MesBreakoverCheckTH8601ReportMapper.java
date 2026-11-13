package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.MesBreakoverCheckTH8601Report;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 导通精密综合测试仪TH8601
 */
@DataSource(value = DataSourceType.SLAVE4)
@Mapper
public interface MesBreakoverCheckTH8601ReportMapper {

    /**
     * 获取数据列表
     * @param report
     * @return
     */
    List<MesBreakoverCheckTH8601Report> selectMesBreakoverCheckTH8601ReportList(MesBreakoverCheckTH8601Report report);
}
