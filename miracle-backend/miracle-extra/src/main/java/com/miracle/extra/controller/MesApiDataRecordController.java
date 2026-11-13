package com.miracle.extra.controller;

import com.miracle.common.annotation.Anonymous;
import com.miracle.common.core.controller.BaseController;

import com.miracle.common.core.page.TableDataInfo;
import com.miracle.common.exception.base.BaseException;
import com.miracle.common.utils.poi.ExcelUtil;
import com.miracle.extra.domain.MesApiDataRecord;
import com.miracle.extra.service.IMesApiDataRecordService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/extra/mes/report/mes_report_api_data_record")
@Anonymous
public class MesApiDataRecordController extends BaseController {

    @Autowired
    private IMesApiDataRecordService mesApiDataRecordService;

    @GetMapping("/list")
    public TableDataInfo list(MesApiDataRecord record) {
        startPage();
        List<MesApiDataRecord> list = mesApiDataRecordService.selectMesApiDataRecordList(record);
        return getDataTable(list);
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, MesApiDataRecord record) {
        List<MesApiDataRecord> list = mesApiDataRecordService.selectMesApiDataRecordList(record);
        if (list.size() > 5000) {
            throw new BaseException("数据量过大，请分批导出或联系管理员导出");
        }
        ExcelUtil<MesApiDataRecord> util = new ExcelUtil<MesApiDataRecord>(MesApiDataRecord.class);
        util.exportExcel(response, list, "MesApi数据记录报表");
    }
}