package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.MesSheBeiZuDataReport;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@DataSource(value = DataSourceType.SLAVE4)
@Mapper
public interface MesSheBeiZuDataReportMapper {
    /**
     * 获取线材导通测试数据列表
     * @param report
     * @return
     */
    List<MesSheBeiZuDataReport> selectMesSheBeiZuDataReportList(MesSheBeiZuDataReport report);
}
