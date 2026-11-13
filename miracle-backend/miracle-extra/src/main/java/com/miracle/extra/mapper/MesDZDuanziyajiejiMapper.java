package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.MesDZDuanziyajieji;
import com.miracle.extra.domain.MesDuanziyajieReport;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 典帧端子压接机报表Mapper
 */
@DataSource(value = DataSourceType.SLAVE5)
@Mapper
public interface MesDZDuanziyajiejiMapper {

    /**
     * 查询典帧端子压接结果列表
     * @param mesDZDuanziyajieji
     * @return
     */
    public List<MesDZDuanziyajieji> selectMesDZDuanziyajiejiList(MesDZDuanziyajieji mesDZDuanziyajieji);
}
