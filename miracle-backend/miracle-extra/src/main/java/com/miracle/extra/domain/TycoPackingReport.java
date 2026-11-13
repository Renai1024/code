package com.miracle.extra.domain;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import com.miracle.common.annotation.Excel;

/** 泰科电脑端追溯报表，按内箱装箱周期展示。 */
public final class TycoPackingReport {
    private TycoPackingReport() { }

    @Data
    public static class Query {
        private String part;
        private String innerCode;
        private String outerCode;
        private String productCode;
        private String status;
        private Boolean tailBox;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate beginDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate endDate;
    }

    /** 当前关联的外箱字段，解绑时间保存在关联表供数据库追溯。 */
    @Data
    public static class Row extends TycoPacking.Session {
        private String outerCode;
        private String outerTraceNo;
        private Integer outerQuantity;
        private Boolean bindingActive;
        private Date boundAt;
    }

    @Data
    public static class Sn {
        private Long id;
        private String productCode;
        private boolean active;
        private Date scannedAt;
    }

    @Data
    public static class Binding {
        private String id;
        private String outerCode;
        private String traceNo;
        private Integer quantity;
        private boolean active;
        private Date boundAt;
    }

    @Data
    public static class Detail {
        private Row box;
        private List<Sn> scans;
        private List<Binding> bindings;
    }

    /** 导出主表使用独立模型，字段顺序及显示值与页面对应。 */
    @Data
    public static class ExportRow {
        @Excel(name = "客户料号", sort = 1)
        private String part;
        @Excel(name = "内箱码", sort = 2, width = 45)
        private String rawCode;
        @Excel(name = "标签数量", sort = 3)
        private Integer quantity;
        @Excel(name = "实际数量", sort = 4, defaultValue = "未记录")
        private Integer packedQuantity;
        @Excel(name = "是否尾箱", sort = 5, readConverterExp = "true=是,false=否")
        private Boolean tailBox;
        @Excel(name = "内箱状态", sort = 6, readConverterExp = "PACKED=已装箱,BOUND=已关联外箱")
        private String status;
        @Excel(name = "外箱追溯码", sort = 7, width = 25)
        private String outerTraceNo;
        @Excel(name = "完整外箱码", sort = 8, width = 60)
        private String outerCode;
        @Excel(name = "外箱关联", sort = 9)
        private String bindingStatus;
        @Excel(name = "外箱数量", sort = 10)
        private Integer outerQuantity;
        @Excel(name = "装箱确认人", sort = 11)
        private String reviewedBy;
        @Excel(name = "装箱完成时间", sort = 12, dateFormat = "yyyy-MM-dd HH:mm:ss", width = 23)
        private Date reviewedAt;
        @Excel(name = "外箱关联时间", sort = 13, dateFormat = "yyyy-MM-dd HH:mm:ss", width = 23)
        private Date boundAt;
    }
}
