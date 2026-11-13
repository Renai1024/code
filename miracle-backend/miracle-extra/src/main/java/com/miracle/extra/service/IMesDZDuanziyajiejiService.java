package com.miracle.extra.service;

import com.miracle.extra.domain.MesDZDuanziyajieji;
import com.miracle.extra.domain.MesDuanziyajieReport;

import java.util.List;

/**
 * 典帧端子压接报表
 */
public interface IMesDZDuanziyajiejiService {

    /**
     * 查询典帧端子压接数据
     * @param mesDZDuanziyajieji
     * @return
     */
    public List<MesDZDuanziyajieji> selectMesDZDuanziyajiejdList(MesDZDuanziyajieji mesDZDuanziyajieji);
}
