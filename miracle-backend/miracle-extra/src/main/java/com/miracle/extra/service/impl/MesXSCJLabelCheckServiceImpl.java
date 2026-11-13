package com.miracle.extra.service.impl;


import com.miracle.extra.domain.MesXSCJLabelCheck;
import com.miracle.extra.mapper.MesXSCJLabelCheckMapper;
import com.miracle.extra.service.IMesXSCJLabelCheckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 线束车间外观检验条码录入
 */
@Service
public class MesXSCJLabelCheckServiceImpl implements IMesXSCJLabelCheckService {

    @Autowired
    private MesXSCJLabelCheckMapper mesXSCJLabelCheckMapper;

    @Override
    public List<MesXSCJLabelCheck> selectMesXSCJLabelCheckList(MesXSCJLabelCheck mesXSCJLabelCheck) {
        return mesXSCJLabelCheckMapper.selectMesXSCJLabelCheckList(mesXSCJLabelCheck);
    }
}
