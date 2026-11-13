package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.MesZhongCeLaoHuaRecordReport;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@DataSource(value = DataSourceType.SLAVE5)
@Mapper
public interface MesZhongCeLaoHuaRecordReportMapper {
    List<MesZhongCeLaoHuaRecordReport> selectMesZhongCeLaoHuaRecordReportList(MesZhongCeLaoHuaRecordReport report);
}