package com.miracle.extra.service;

import com.miracle.extra.domain.MesXSCJLabelCheck;

import java.util.List;

/**
 * 线束车间外观检验条码录入
 */
public interface IMesXSCJLabelCheckService {

    /**
     * 获取条码录入数据列表
     * @param mesXSCJLabelCheck
     * @return
     */
    public List<MesXSCJLabelCheck> selectMesXSCJLabelCheckList(MesXSCJLabelCheck mesXSCJLabelCheck);
}
