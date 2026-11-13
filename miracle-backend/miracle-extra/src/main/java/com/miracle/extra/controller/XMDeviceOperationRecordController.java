package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.core.controller.BaseController;
import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.XMDeviceOperationRecord;
import com.miracle.extra.service.IXMDeviceOperationRecordService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/extra/mes/report/mes_report_xm_device_operation_record")
@Anonymous
public class XMDeviceOperationRecordController extends BaseController {

    @Autowired
    private IXMDeviceOperationRecordService xmDeviceOperationRecordService;

    @GetMapping("/list")
    public TableDataInfo list(XMDeviceOperationRecord record) {
        startPage();
        List<XMDeviceOperationRecord> list = xmDeviceOperationRecordService.selectXMDeviceOperationRecordList(record);
        return getDataTable(list);
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, XMDeviceOperationRecord record) {
        List<XMDeviceOperationRecord> list = xmDeviceOperationRecordService.selectXMDeviceOperationRecordList(record);
        if (list.size() > 5000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<XMDeviceOperationRecord> util = new ExcelUtil<XMDeviceOperationRecord>(XMDeviceOperationRecord.class);
        util.exportExcel(response, list, "XM设备操作记录报表");
    }
}