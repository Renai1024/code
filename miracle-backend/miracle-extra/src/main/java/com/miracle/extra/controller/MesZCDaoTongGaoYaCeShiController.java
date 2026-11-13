package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.annotation.Log;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.enums.BusinessType;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.MesLWSQiMiXingReport;
import com.miracle.extra.domain.MesZCDaoTongGaoYaCeShi;
import com.miracle.extra.service.IMesLWSQiMiXingReportService;
import com.miracle.extra.service.IMesZCDaoTongGaoYaCeShiService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Anonymous
@RequestMapping("/extra/mes/report/mes_report_zcDaoTongGaoYaCeShi")
public class MesZCDaoTongGaoYaCeShiController extends BaseController {

    @Autowired
    private IMesZCDaoTongGaoYaCeShiService mesZCDaoTongGaoYaCeShiService;

    /**
     * 查询中测导通高压测试数据列表
     * @param mesZCDaoTongGaoYaCeShi
     * @return
     */
    @GetMapping("/list")
    public TableDataInfo list(MesZCDaoTongGaoYaCeShi mesZCDaoTongGaoYaCeShi){
        startPage();
        List<MesZCDaoTongGaoYaCeShi> list = mesZCDaoTongGaoYaCeShiService.selectMesZCDaoTongGaoYaCeShiList(mesZCDaoTongGaoYaCeShi);
        return getDataTable(list);
    }

    /**
     * 导出中测导通高压测试列表
     */
    @Log(title = "zcDaoTongGaoYaCeShi", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void mesMesZCDaoTongGaoYaCeShiExport(HttpServletResponse response, MesZCDaoTongGaoYaCeShi mesZCDaoTongGaoYaCeShi)
    {
        List<MesZCDaoTongGaoYaCeShi> list = mesZCDaoTongGaoYaCeShiService.selectMesZCDaoTongGaoYaCeShiList(mesZCDaoTongGaoYaCeShi);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesZCDaoTongGaoYaCeShi> util = new ExcelUtil<MesZCDaoTongGaoYaCeShi>(MesZCDaoTongGaoYaCeShi.class);
        util.exportExcel(response, list, "zcDaoTongGaoYaCeShi数据");
    }
}
