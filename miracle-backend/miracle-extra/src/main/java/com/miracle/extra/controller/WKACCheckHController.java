package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.annotation.Log;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.enums.BusinessType;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.FLACCheckH;
import com.miracle.extra.domain.WKACCheckH;
import com.miracle.extra.service.IFLACCheckHService;
import com.miracle.extra.service.IWKACCheckHService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Anonymous
@RequestMapping("/extra/mes/report/wkaccheckH")
public class WKACCheckHController extends BaseController {

    @Autowired
    private IWKACCheckHService wkACCheckHService;

    /**
     * 查询万肯(有码)耐压测试数据列表
     * @param wkACCheckH
     * @return
     */
    @GetMapping("/wkyouma/list")
    public TableDataInfo WKyouma_List(WKACCheckH wkACCheckH){
        startPage();
        List<WKACCheckH> list = wkACCheckHService.selectWKyouma_WKACCheckHList(wkACCheckH);
        return getDataTable(list);
    }

    /**
     * 导出万肯(有码)测试数据列表
     */
    @Log(title = "WKyouma_ACCheckH", businessType = BusinessType.EXPORT)
    @PostMapping("/wkyouma/export")
    public void WKyouma_Export(HttpServletResponse response, WKACCheckH wkACCheckH)
    {
        List<WKACCheckH> list = wkACCheckHService.selectWKyouma_WKACCheckHList(wkACCheckH);
        if (list.size() > 10000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<WKACCheckH> util = new ExcelUtil<WKACCheckH>(WKACCheckH.class);
        util.exportExcel(response, list, "万肯耐压(有码)测试数据");
    }

    /**
     * 查询安费诺耐压测试数据列表
     * @param wkACCheckH
     * @return
     */
    @GetMapping("/afn/list")
    public TableDataInfo AFN_List(WKACCheckH wkACCheckH){
        startPage();
        List<WKACCheckH> list = wkACCheckHService.selectAFN_WKACCheckHList(wkACCheckH);
        return getDataTable(list);
    }

    /**
     * 导出安费诺测试数据列表
     */
    @Log(title = "AFN_ACCheckH", businessType = BusinessType.EXPORT)
    @PostMapping("/afn/export")
    public void AFN_Export(HttpServletResponse response, WKACCheckH wkACCheckH)
    {
        List<WKACCheckH> list = wkACCheckHService.selectAFN_WKACCheckHList(wkACCheckH);
        if (list.size() > 50000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<WKACCheckH> util = new ExcelUtil<WKACCheckH>(WKACCheckH.class);
        util.exportExcel(response, list, "安费诺测试数据");
    }

    /**
     * 查询泰科电阻测试数据列表
     * @param wkACCheckH
     * @return
     */
    @GetMapping("/tkdr/list")
    public TableDataInfo TKdr_List(WKACCheckH wkACCheckH){
        startPage();
        List<WKACCheckH> list = wkACCheckHService.selectTKdr_WKACCheckHList(wkACCheckH);
        return getDataTable(list);
    }

    /**
     * 导出泰科电阻测试数据列表
     */
    @Log(title = "TKDR_ACCheckH", businessType = BusinessType.EXPORT)
    @PostMapping("/tkdr/export")
    public void TKdr_Export(HttpServletResponse response, WKACCheckH wkACCheckH)
    {
        List<WKACCheckH> list = wkACCheckHService.selectTKdr_WKACCheckHList(wkACCheckH);
        if (list.size() > 50000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<WKACCheckH> util = new ExcelUtil<WKACCheckH>(WKACCheckH.class);
        util.exportExcel(response, list, "泰科电阻测试数据");
    }

}
