package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.MesDianCeXianReport;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 电测线报表Mapper
 */
@DataSource(value = DataSourceType.SLAVE5)
@Mapper
public interface MesDianCeXianReportMapper {

    /**
     * 查询电测线结果列表
     * @param mesDianCeXianReport
     * @return
     */
    public List<MesDianCeXianReport> selectMesDianCeXianReportList(MesDianCeXianReport mesDianCeXianReport);
}
