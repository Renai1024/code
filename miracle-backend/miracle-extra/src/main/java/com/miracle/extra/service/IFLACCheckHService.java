package com.miracle.extra.service;

import com.miracle.extra.domain.FLACCheckH;
import com.miracle.extra.domain.MesDianCeXianReport;
import com.miracle.extra.domain.WKCheckPrintCpkReport;

import java.util.List;

/**
 * FLACCheckH报表
 */
public interface IFLACCheckHService {

    /**
     * 查询法拉耐压测试数据列表
     * @param fLACCheckH
     * @return
     */
    public List<FLACCheckH> selectXMFL_FLACCheckHList(FLACCheckH fLACCheckH);

    /**
     * 查询万肯无码耐压测试数据列表
     * @param fLACCheckH
     * @return
     */
    public List<FLACCheckH> selectWKwuma_FLACCheckHList(FLACCheckH fLACCheckH);

    /**
     * 查询东电化耐压测试数据列表
     * @param fLACCheckH
     * @return
     */
    public List<FLACCheckH> selectDDH_FLACCheckHList(FLACCheckH fLACCheckH);


    /**
     * 查询万肯气密性测试数据列表
     * @param fLACCheckH
     * @return
     */
    public List<FLACCheckH> selectWKCheckPrintList(FLACCheckH fLACCheckH);

    /**
     * 计算单个工单号的CPK分析报告
     * @param workOrderNo 生产工单号
     * @param deviceNo 设备编号（可选）
     * @param startTime 检测开始时间（可选）
     * @param endTime 检测结束时间（可选）
     * @return CPK分析报告
     */
    public WKCheckPrintCpkReport calculateCpkByWorkOrder(String workOrderNo, String deviceNo, java.util.Date startTime, java.util.Date endTime);

}
