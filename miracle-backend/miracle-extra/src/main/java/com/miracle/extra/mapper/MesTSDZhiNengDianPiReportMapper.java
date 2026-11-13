package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.MesDianCeXianReport;
import com.miracle.extra.domain.MesTSDZhiNengDianPiReport;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * TSD智能电批报表Mapper
 */
@DataSource(value = DataSourceType.SLAVE5)
@Mapper
public interface MesTSDZhiNengDianPiReportMapper {

    /**
     * 查询TSD智能电批结果列表
     * @param mesTSDZhiNengDianPiReport
     * @return
     */
    public List<MesTSDZhiNengDianPiReport> selectMesTSDZhiNengDianPiReportList(MesTSDZhiNengDianPiReport mesTSDZhiNengDianPiReport);
}
