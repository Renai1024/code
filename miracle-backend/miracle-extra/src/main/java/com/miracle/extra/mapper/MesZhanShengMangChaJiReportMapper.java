package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.MesZhanShengMangChaJiReport;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@DataSource(value = DataSourceType.SLAVE5)
@Mapper
public interface MesZhanShengMangChaJiReportMapper {
    List<MesZhanShengMangChaJiReport> selectMesZhanShengMangChaJiReportList(MesZhanShengMangChaJiReport report);
}