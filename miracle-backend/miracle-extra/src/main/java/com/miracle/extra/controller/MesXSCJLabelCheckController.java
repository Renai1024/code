package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.annotation.Log;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.enums.BusinessType;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.MesXSCJLabelCheck;
import com.miracle.extra.service.IMesXSCJLabelCheckService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Anonymous
@RequestMapping("/extra/mes/report/mes_report_xscj")
public class MesXSCJLabelCheckController extends BaseController {

    @Autowired
    private IMesXSCJLabelCheckService mesXSCJLabelCheckService;

    /**
     * 查询线束车间外观检测扫码录入数据列表
     * @param mesXSCJLabelCheck
     * @return
     */
    @GetMapping("/list")
    public TableDataInfo list(MesXSCJLabelCheck mesXSCJLabelCheck){
        startPage();
        List<MesXSCJLabelCheck> list = mesXSCJLabelCheckService.selectMesXSCJLabelCheckList(mesXSCJLabelCheck);
        return getDataTable(list);
    }

    /**
     * 导出线束车间外观检测扫码录入列表
     */
    @Log(title = "mesXSCJLabelCheckReport", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void mesDianCeXianReportExport(HttpServletResponse response, MesXSCJLabelCheck mesXSCJLabelCheck)
    {
        List<MesXSCJLabelCheck> list = mesXSCJLabelCheckService.selectMesXSCJLabelCheckList(mesXSCJLabelCheck);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesXSCJLabelCheck> util = new ExcelUtil<MesXSCJLabelCheck>(MesXSCJLabelCheck.class);
        util.exportExcel(response, list, "线束车间外观检验条码录入数据");
    }
}
