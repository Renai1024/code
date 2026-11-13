package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.MesMuPai3DataReport;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@DataSource(value = DataSourceType.SLAVE4)
@Mapper
public interface MesMuPai3DataReportMapper {

    /**
     * 查询MuPai3Data数据列表
     * @param report
     * @return
     */
    List<MesMuPai3DataReport> selectMesMuPai3DataReportList(MesMuPai3DataReport report);
}
