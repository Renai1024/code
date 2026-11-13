package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.MesDianJiaoJiRecordReport;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 点胶机报表Mapper
 */
@DataSource(value = DataSourceType.SLAVE5)
@Mapper
public interface MesDianJiaoJiRecordReportMapper {

    /**
     * 查询点胶结果列表
     * @param mesDianJiaoJiRecordReport
     * @return
     */
    public List<MesDianJiaoJiRecordReport> selectMesDianJiaoJiRecordReportList(MesDianJiaoJiRecordReport mesDianJiaoJiRecordReport);
}
