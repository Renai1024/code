package com.miracle.extra.service.impl;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.beans.BeanUtils;
import com.github.pagehelper.PageHelper;
import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.TycoPackingReport.*;
import com.miracle.extra.mapper.TycoPackingReportMapper;
import com.miracle.extra.service.ITycoPackingReportService;

/** 列表、详情和 Excel 仅提供有效装箱数据，历史保留在数据库。 */
@Service
@DataSource(DataSourceType.SLAVE2)
public class TycoPackingReportServiceImpl implements ITycoPackingReportService {
    private final TycoPackingReportMapper mapper;

    public TycoPackingReportServiceImpl(TycoPackingReportMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<Row> list(Query query) {
        if (query.getBeginDate()!=null && query.getEndDate()!=null && query.getBeginDate().isAfter(query.getEndDate())) {
            throw new IllegalArgumentException("开始日期不能晚于结束日期");
        }
        if (query.getStatus()!=null && !query.getStatus().isEmpty()
                && !Set.of("PACKED","BOUND").contains(query.getStatus())) {
            throw new IllegalArgumentException("装箱状态无效");
        }
        return mapper.list(query);
    }

    /** 同一筛选查询最多读取上限加一条，超限时提示缩小范围，防止无界加载。 */
    @Override
    public List<ExportRow> export(Query query) {
        List<Row> rows;
        try {
            PageHelper.startPage(1,10001,false);
            rows = list(query);
        } finally {
            PageHelper.clearPage();
        }
        if (rows.size()>10000) throw new IllegalArgumentException("单次最多导出10000条，请缩小筛选范围后重试");
        return rows.stream().map(row -> {
            ExportRow result = new ExportRow();
            BeanUtils.copyProperties(row,result);
            // 有效旧内箱未保存实际数量快照时，沿用页面的标签数量回退规则。
            if (result.getPackedQuantity()==null) {
                result.setPackedQuantity(row.getQuantity());
            }
            result.setBindingStatus(Boolean.TRUE.equals(row.getBindingActive()) ? "已关联" : "未关联");
            return result;
        }).toList();
    }

    @Override
    public Detail detail(String id) {
        if (id==null || !UUID.fromString(id).toString().equalsIgnoreCase(id)) {
            throw new IllegalArgumentException("装箱记录编号无效");
        }
        Row box = mapper.box(id);
        if (box==null) throw new IllegalArgumentException("装箱记录不存在");
        Detail result = new Detail();
        result.setBox(box);
        result.setScans(mapper.scans(id));
        result.setBindings(mapper.bindings(id));
        return result;
    }
}
