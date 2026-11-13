package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.annotation.Log;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.enums.BusinessType;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.FLACCheckH;
import com.miracle.extra.domain.MesDianCeXianReport;
import com.miracle.extra.domain.WKCheckPrintCpkReport;
import com.miracle.extra.service.IFLACCheckHService;
import com.miracle.extra.service.IMesDianCeXianReportService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * FLACCheckH报表控制器
 * <p>
 * 提供法拉耐压测试、万肯耐压测试、东电化耐压测试、万肯气密性测试等数据的查询、导出和CPK分析功能
 * </p>
 */
@RestController
@Anonymous
@RequestMapping("/extra/mes/report/flaccheckH")
public class FLACCheckHController extends BaseController {

    @Autowired
    private IFLACCheckHService fLACCheckHService;

    /**
     * 查询法拉耐压测试数据列表
     * <p>
     * 支持分页查询，CustomerFlag固定为"XMFL"
     * </p>
     * 
     * @param fLACCheckH 查询条件，支持按ResultStr、MyField1、StockInBarCode、MyField2、MyField3、MyField4、时间范围等条件筛选
     * @return 分页数据
     */
    @GetMapping("/xmfl/list")
    public TableDataInfo XMFL_List(FLACCheckH fLACCheckH){
        startPage();
        List<FLACCheckH> list = fLACCheckHService.selectXMFL_FLACCheckHList(fLACCheckH);
        return getDataTable(list);
    }

    /**
     * 导出法拉耐压测试数据列表
     */
    @Log(title = "XMFL_ACCheckH", businessType = BusinessType.EXPORT)
    @PostMapping("/xmfl/export")
    public void XMFL_Export(HttpServletResponse response, FLACCheckH fLACCheckH)
    {
        List<FLACCheckH> list = fLACCheckHService.selectXMFL_FLACCheckHList(fLACCheckH);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<FLACCheckH> util = new ExcelUtil<FLACCheckH>(FLACCheckH.class);
        util.exportExcel(response, list, "法拉耐压测试数据");
    }

    /**
     * 查询万肯耐压（无码）测试数据列表
     * <p>
     * 支持分页查询，CustomerFlag固定为"WKwuma"
     * </p>
     * 
     * @param fLACCheckH 查询条件，支持按ResultStr、MyField1、StockInBarCode、MyField2、MyField3、MyField4、时间范围等条件筛选
     * @return 分页数据
     */
    @GetMapping("/wkwuma/list")
    public TableDataInfo WKwuma_List(FLACCheckH fLACCheckH){
        startPage();
        List<FLACCheckH> list = fLACCheckHService.selectWKwuma_FLACCheckHList(fLACCheckH);
        return getDataTable(list);
    }

    /**
     * 导出万肯耐压（无码）测试数据列表
     */
    @Log(title = "WKwuma_ACCheckH", businessType = BusinessType.EXPORT)
    @PostMapping("/wkwuma/export")
    public void WKwuma_Export(HttpServletResponse response, FLACCheckH fLACCheckH)
    {
        List<FLACCheckH> list = fLACCheckHService.selectWKwuma_FLACCheckHList(fLACCheckH);
        if (list.size() > 50000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<FLACCheckH> util = new ExcelUtil<FLACCheckH>(FLACCheckH.class);
        util.exportExcel(response, list, "万肯耐压（无码）测试数据");
    }

    /**
     * 查询东电化耐压测试数据列表
     * <p>
     * 支持分页查询，CustomerFlag固定为"DDH"
     * </p>
     * 
     * @param fLACCheckH 查询条件，支持按ResultStr、MyField1、StockInBarCode、MyField2、MyField3、MyField4、时间范围等条件筛选
     * @return 分页数据
     */
    @GetMapping("/ddh/list")
    public TableDataInfo DDH_List(FLACCheckH fLACCheckH){
        startPage();
        List<FLACCheckH> list = fLACCheckHService.selectDDH_FLACCheckHList(fLACCheckH);
        return getDataTable(list);
    }

    /**
     * 导出东电化测试数据列表
     */
    @Log(title = "DDH_ACCheckH", businessType = BusinessType.EXPORT)
    @PostMapping("/ddh/export")
    public void DDH_Export(HttpServletResponse response, FLACCheckH fLACCheckH)
    {
        List<FLACCheckH> list = fLACCheckHService.selectDDH_FLACCheckHList(fLACCheckH);
        if (list.size() > 50000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<FLACCheckH> util = new ExcelUtil<FLACCheckH>(FLACCheckH.class);
        util.exportExcel(response, list, "东电化耐压测试数据");
    }

    /**
     * 查询万肯气密性测试数据列表
     * <p>
     * 支持分页查询，CustomerFlag为WK1、WK2、WK3、WK4之一
     * </p>
     * 
     * @param fLACCheckH 查询条件，支持按ResultStr、MyField1、StockInBarCode、MyField2、MyField3、MyField4、时间范围等条件筛选
     * @return 分页数据
     */
    @GetMapping("/wkCheckPrint/list")
    public TableDataInfo WKCheckPrintList(FLACCheckH fLACCheckH){
        startPage();
        List<FLACCheckH> list = fLACCheckHService.selectWKCheckPrintList(fLACCheckH);
        return getDataTable(list);
    }

    /**
     * 导出万肯气密性测试数据列表
     */
    @Log(title = "WK_CheckPrint", businessType = BusinessType.EXPORT)
    @PostMapping("/wkCheckPrint/export")
    public void WKCheckPrintExport(HttpServletResponse response, FLACCheckH fLACCheckH)
    {
        List<FLACCheckH> list = fLACCheckHService.selectWKCheckPrintList(fLACCheckH);
        if (list.size() > 50000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<FLACCheckH> util = new ExcelUtil<FLACCheckH>(FLACCheckH.class);
        util.exportExcel(response, list, "万肯气密性测试数据");
    }

    /**
     * 计算单个工单号的CPK分析报告
     * <p>
     * 基于万肯气密性测试数据，按生产工单号进行CPK计算分析
     * </p>
     * <p>
     * 计算逻辑：
     * 1. 从ResultStr中提取泄漏量数值（格式：泄漏量结果：数值Pa）
     * 2. 将泄漏量转换为绝对值后计算均值和标准差
     * 3. CPK计算公式：CPK = (控制标准上限 - 实测样本均值) / (3 * 实测样本标准差)
     * 4. 只统计CustomerFlag为WK1/WK2/WK3/WK4且MyField2（控制上限）有效的数据
     * </p>
     * 
     * @param workOrderNo 生产工单号（必填），对应FLACCheckH表的MyField3字段
     * @param deviceNo 设备编号（可选），对应FLACCheckH表的MyField4字段，用于限定特定设备
     * @param startTime 检测开始时间（可选），对应FLACCheckH表的CheckStartTime字段
     * @param endTime 检测结束时间（可选），对应FLACCheckH表的CheckStartTime字段
     * @return CPK分析报告，包含工单号、设备编号、控制上限、样本数、合格率、均值、标准差、CPK值等信息
     */
    @GetMapping("/wkCheckPrint/cpk")
    public WKCheckPrintCpkReport calculateCpkByWorkOrder(String workOrderNo, String deviceNo, 
                                                         java.util.Date startTime, java.util.Date endTime) {
        return fLACCheckHService.calculateCpkByWorkOrder(workOrderNo, deviceNo, startTime, endTime);
    }
}