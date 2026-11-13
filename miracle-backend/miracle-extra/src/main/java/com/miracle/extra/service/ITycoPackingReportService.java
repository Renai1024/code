package com.miracle.extra.service;

import java.util.List;
import com.miracle.extra.domain.TycoPackingReport.*;

/** 泰科装箱追溯查询接口。 */
public interface ITycoPackingReportService {
    List<Row> list(Query query);
    List<ExportRow> export(Query query);
    Detail detail(String id);
}
