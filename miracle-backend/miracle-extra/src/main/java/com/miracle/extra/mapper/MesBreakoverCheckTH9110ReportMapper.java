package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.MesBreakoverCheckTH9110Report;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 绝缘电阻测试仪TH9110
 */
@DataSource(value = DataSourceType.SLAVE4)
@Mapper
public interface MesBreakoverCheckTH9110ReportMapper {
    /**
     * 获取数据列表
     * @param report
     * @return
     */
    List<MesBreakoverCheckTH9110Report> selectMesBreakoverCheckTH9110ReportList(MesBreakoverCheckTH9110Report report);
}
