package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.MesXSCJLabelCheck;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 线束车间外观检验条码录入
 */
@DataSource(value = DataSourceType.SLAVE4)
@Mapper
public interface MesXSCJLabelCheckMapper {

    /**
     * 获取条码录入数据列表
     * @param mesXSCJLabelCheck
     * @return
     */
    public List<MesXSCJLabelCheck> selectMesXSCJLabelCheckList(MesXSCJLabelCheck mesXSCJLabelCheck);
}
