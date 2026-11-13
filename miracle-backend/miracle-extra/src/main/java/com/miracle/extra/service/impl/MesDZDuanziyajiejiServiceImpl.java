package com.miracle.extra.service.impl;

import com.miracle.extra.domain.MesDZDuanziyajieji;
import com.miracle.extra.mapper.MesDZDuanziyajiejiMapper;
import com.miracle.extra.service.IMesDZDuanziyajiejiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 典帧端子压接机报表服务实现类
 */
@Service
public class MesDZDuanziyajiejiServiceImpl implements IMesDZDuanziyajiejiService {

    @Autowired
    private MesDZDuanziyajiejiMapper mesDZDuanziyajiejiMapper;

    @Override
    public List<MesDZDuanziyajieji> selectMesDZDuanziyajiejdList(MesDZDuanziyajieji mesDZDuanziyajieji) {
        return mesDZDuanziyajiejiMapper.selectMesDZDuanziyajiejiList(mesDZDuanziyajieji);
    }
}
