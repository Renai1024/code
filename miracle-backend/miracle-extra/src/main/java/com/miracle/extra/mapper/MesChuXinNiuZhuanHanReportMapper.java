package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.MesChuXinNiuZhuanHanReport;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 楚鑫扭转焊数据
 */
@DataSource(value = DataSourceType.SLAVE5)
@Mapper
public interface MesChuXinNiuZhuanHanReportMapper {

    /**
     * 获取扭转机数据列表
     * @param report
     * @return
     */
    List<MesChuXinNiuZhuanHanReport> selectMesChuXinNiuZhuanHanReportList(MesChuXinNiuZhuanHanReport report);

    /**
     * 获取端子焊接机数据列表
     * @param report
     * @return
     */
    List<MesChuXinNiuZhuanHanReport> selectMesChuXinDuanZiHanReportList(MesChuXinNiuZhuanHanReport report);

    /**
     * 获取超声波焊接机数据列表
     * @param report
     * @return
     */
    List<MesChuXinNiuZhuanHanReport> selectMesChuXinChaoShengBoHanReportList(MesChuXinNiuZhuanHanReport report);
}