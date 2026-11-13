package com.miracle.extra.service.impl;

import com.miracle.extra.domain.FLACCheckH;
import com.miracle.extra.domain.MesDianCeXianReport;
import com.miracle.extra.domain.WKCheckPrintCpkReport;
import com.miracle.extra.mapper.FLACCheckHMapper;
import com.miracle.extra.mapper.MesDianCeXianReportMapper;
import com.miracle.extra.service.IFLACCheckHService;
import com.miracle.extra.service.IMesDianCeXianReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FLACCheckH表服务实现类
 * <p>
 * 提供法拉耐压测试、万肯耐压测试、东电化耐压测试、万肯气密性测试等数据的查询和CPK分析功能
 * </p>
 */
@Service
public class FLACCheckHServiceImpl implements IFLACCheckHService {

    @Autowired
    private FLACCheckHMapper flacCheckHMapper;


    @Override
    public List<FLACCheckH> selectXMFL_FLACCheckHList(FLACCheckH fLACCheckH) {
        fLACCheckH.setCustomerFlag("XMFL");
        List<FLACCheckH> list =  flacCheckHMapper.selectFLACCheckHList(fLACCheckH);
        return list;
    }

    @Override
    public List<FLACCheckH> selectWKwuma_FLACCheckHList(FLACCheckH fLACCheckH) {
        fLACCheckH.setCustomerFlag("WKwuma");
        List<FLACCheckH> list =  flacCheckHMapper.selectFLACCheckHList(fLACCheckH);
        return list;
    }

    @Override
    public List<FLACCheckH> selectDDH_FLACCheckHList(FLACCheckH fLACCheckH) {
        fLACCheckH.setCustomerFlag("DDH");
        List<FLACCheckH> list =  flacCheckHMapper.selectFLACCheckHList(fLACCheckH);
        return list;
    }

    @Override
    public List<FLACCheckH> selectWKCheckPrintList(FLACCheckH fLACCheckH) {
        List<FLACCheckH> list =  flacCheckHMapper.selectWKCheckPrintList(fLACCheckH);
        return list;
    }

    @Override
    public WKCheckPrintCpkReport calculateCpkByWorkOrder(String workOrderNo, String deviceNo, Date startTime, Date endTime) {
        FLACCheckH query = new FLACCheckH();
        query.setMyField3(workOrderNo);
        query.setMyField4(deviceNo);
        query.setTestStartTime(startTime);
        query.setTestEndTime(endTime);
        
        List<FLACCheckH> dataList = flacCheckHMapper.selectWKCheckPrintList(query);
        
        return calculateCpkReport(dataList, workOrderNo, deviceNo);
    }

    /**
     * 计算CPK分析报告
     * <p>
     * 处理流程：
     * 1. 遍历查询到的FLACCheckH数据列表
     * 2. 过滤无效数据：MyField2（控制上限）为空或无法解析为数字的数据
     * 3. 从ResultStr中提取泄漏量数值，格式为"泄漏量结果：数值Pa"
     * 4. 将泄漏量转换为绝对值后存入有效样本列表
     * 5. 根据ResultStr是否包含"合格"统计合格/不合格数量
     * 6. 计算合格率、均值、标准差、CPK值、最小值、最大值
     * </p>
     * 
     * @param dataList 查询到的FLACCheckH数据列表
     * @param workOrderNo 生产工单号
     * @param deviceNo 设备编号
     * @return CPK分析报告
     */
    private WKCheckPrintCpkReport calculateCpkReport(List<FLACCheckH> dataList, String workOrderNo, String deviceNo) {
        WKCheckPrintCpkReport report = new WKCheckPrintCpkReport();
        report.setWorkOrderNo(workOrderNo);
        report.setDeviceNo(deviceNo);
        
        List<Double> validLeakValues = new ArrayList<>();
        int qualifiedCount = 0;
        int unqualifiedCount = 0;
        Double controlUpperLimit = null;
        
        // 最大连续不良数相关变量
        int maxConsecutiveDefects = 0;
        int currentConsecutiveDefects = 0;
        Date currentDefectStartTime = null;
        Date currentDefectEndTime = null;
        Date maxDefectStartTime = null;
        Date maxDefectEndTime = null;
        
        Pattern leakPattern = Pattern.compile("泄漏量结果：([-+]?[0-9]*\\.?[0-9]+)Pa");

        for (FLACCheckH data : dataList) {
            String resultStr = data.getResultStr();
            String myField2 = data.getMyField2();
            
            if (myField2 == null || myField2.trim().isEmpty()) {
                continue;
            }
            Double upperLimit;
            try {
                upperLimit = Double.parseDouble(myField2.trim());
                if (upperLimit > 100 || upperLimit <= 0)
                    continue;
            } catch (NumberFormatException e) {
                continue;
            }
            
            if (controlUpperLimit == null) {
                controlUpperLimit = upperLimit;
            }
            
            if (resultStr == null || resultStr.isEmpty()) {
                continue;
            }
            
            Matcher matcher = leakPattern.matcher(resultStr);
            if (!matcher.find()) {
                continue;
            }
            
            Double leakValue;
            try {
                leakValue = Math.abs(Double.parseDouble(matcher.group(1)));
            } catch (NumberFormatException e) {
                continue;
            }

            //数据清洗保留有效数据
            boolean isQualified = resultStr.contains("合格") && leakValue <= controlUpperLimit;
            boolean isUnqualified = resultStr.contains("不合格") || resultStr.contains("严重泄漏");
            
            if (isUnqualified) {
                unqualifiedCount++;
                // 更新连续不良计数
                currentConsecutiveDefects++;
                Date checkEndTime = data.getCheckEndTime() != null ? data.getCheckEndTime() : data.getCheckStartTime();
                currentDefectEndTime = checkEndTime;
                // 如果是连续不良的第一个，记录开始时间（使用 CheckStartTime）
                if (currentDefectStartTime == null) {
                    currentDefectStartTime = data.getCheckStartTime();
                }
            } else if (isQualified) {
                qualifiedCount++;
                // 遇到合格产品，检查并更新最大连续不良数
                if (currentConsecutiveDefects > maxConsecutiveDefects) {
                    maxConsecutiveDefects = currentConsecutiveDefects;
                    maxDefectStartTime = currentDefectStartTime;
                    maxDefectEndTime = currentDefectEndTime;
                }
                // 重置当前连续不良计数
                currentConsecutiveDefects = 0;
                currentDefectStartTime = null;
                currentDefectEndTime = null;
            } else {
                // 既不是合格也不是不合格，重置连续不良计数
                if (currentConsecutiveDefects > maxConsecutiveDefects) {
                    maxConsecutiveDefects = currentConsecutiveDefects;
                    maxDefectStartTime = currentDefectStartTime;
                    maxDefectEndTime = currentDefectEndTime;
                }
                currentConsecutiveDefects = 0;
                currentDefectStartTime = null;
                currentDefectEndTime = null;
                continue;
            }
            validLeakValues.add(leakValue);
        }
        
        // 循环结束后，检查最后一组连续不良
        if (currentConsecutiveDefects > maxConsecutiveDefects) {
            maxConsecutiveDefects = currentConsecutiveDefects;
            maxDefectStartTime = currentDefectStartTime;
            maxDefectEndTime = currentDefectEndTime;
        }
        
        report.setControlUpperLimit(controlUpperLimit);
        report.setSampleCount(validLeakValues.size());
        report.setQualifiedCount(qualifiedCount);
        report.setUnqualifiedCount(unqualifiedCount);
        // 设置最大连续不良数及时间段
        report.setMaxConsecutiveDefects(maxConsecutiveDefects);
        report.setMaxConsecutiveDefectsStartTime(maxDefectStartTime);
        report.setMaxConsecutiveDefectsEndTime(maxDefectEndTime);
        
        if (qualifiedCount + unqualifiedCount > 0) {
            report.setQualifiedRate(qualifiedCount * 10000.0 / (qualifiedCount + unqualifiedCount) / 100.0);
        } else {
            report.setQualifiedRate(0.0);
        }
        
        if (validLeakValues.size() >= 2 && controlUpperLimit != null) {
            double mean = calculateMean(validLeakValues);
            double stdDev = calculateStdDeviation(validLeakValues, mean);
            
            report.setMeanValue(Math.round(mean * 1000000.0) / 1000000.0);
            report.setStdDeviation(Math.round(stdDev * 1000000.0) / 1000000.0);
            
            if (stdDev > 0) {
                double cpk = (controlUpperLimit - mean) / (3 * stdDev);
                report.setCpkValue(Math.round(cpk * 1000.0) / 1000.0);
            } else {
                report.setCpkValue(null);
            }
            
            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for (Double value : validLeakValues) {
                if (value < min){
                    min = value;
                }
                if (value > max){
                    max = value;
                }
            }
            report.setMinValue(Math.round(min * 1000000.0) / 1000000.0);
            report.setMaxValue(Math.round(max * 1000000.0) / 1000000.0);
        }
        
        return report;
    }

    /**
     * 计算均值
     * <p>
     * 计算公式：mean = Σvalue / n
     * </p>
     * 
     * @param values 数值列表
     * @return 均值
     */
    private double calculateMean(List<Double> values) {
        double sum = 0;
        for (Double value : values) {
            sum += value;
        }
        return sum / values.size();
    }

    /**
     * 计算标准差（样本标准差，除以n-1）
     * <p>
     * 计算公式：stdDev = √[Σ(value - mean)² / (n - 1)]
     * 使用样本标准差而非总体标准差，更符合统计学要求
     * </p>
     * 
     * @param values 数值列表
     * @param mean 均值
     * @return 标准差
     */
    private double calculateStdDeviation(List<Double> values, double mean) {
        double sumSquaredDiff = 0;
        for (Double value : values) {
            double diff = value - mean;
            sumSquaredDiff += diff * diff;
        }
        return Math.sqrt(sumSquaredDiff / (values.size() - 1));
    }
}