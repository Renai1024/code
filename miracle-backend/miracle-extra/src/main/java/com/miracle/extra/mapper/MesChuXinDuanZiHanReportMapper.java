package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.MesChuXinDuanZiHanReport;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 楚鑫端子焊接机数据
 */
@DataSource(value = DataSourceType.SLAVE5)
@Mapper
public interface MesChuXinDuanZiHanReportMapper {

    /**
     * 获取数据列表
     * @param report
     * @return
     */
    List<MesChuXinDuanZiHanReport> selectMesChuXinDuanZiHanReportList(MesChuXinDuanZiHanReport report);
}