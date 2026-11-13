package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MesTSDZhiNengDianPiReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 入库时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "入库时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 条码（SN） */
    @Excel(name = "条码")
    private String sn;

    /** 完成时间（字符串格式） */
    @Excel(name = "完成时间", width = 30)
    private String finishTime;

    /** 当前螺钉情况（测试结果） */
    @Excel(name = "结果")
    private String result;

    /** 设备识别码 */
    @Excel(name = "设备识别码")
    private String mesId; // 对应数据库 mes_id

    /** 螺钉序号 */
    @Excel(name = "螺钉序号")
    private String demoData1;

    /** 扭力 */
    @Excel(name = "扭力")
    private String demoData2;

    /** 圈数 */
    @Excel(name = "圈数")
    private String demoData3;

    /** 运行时间 */
    @Excel(name = "运行时间")
    private String demoData4;

    // ========== 查询条件字段（非数据库字段）==========
    /** 完成时间开始 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishStartTime;

    /** 完成时间结束 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishEndTime;
}
