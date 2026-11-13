package com.miracle.extra.domain;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class MesZhanShengMangChaJiReport implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Integer id;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /** 编码 */
    @Excel(name = "编码")
    private String code;

    /** 上传时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "上传时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;

    /** 机器编码 */
    @Excel(name = "机器编码")
    private String macheineCode;

    /** 数据JSON */
    @Excel(name = "数据")
    private String datas;

    /** 格式化后的数据（用于导出） */
    @Excel(name = "颜色数据", width = 50)
    private String formattedDatas;

    /** 解析后的数据列表（用于前端展示） */
    private List<ColorData> dataList;

    /** 内部类：颜色数据 */
    @Data
    public static class ColorData {
        @JSONField(name = "Color")
        private String color;
        
        @JSONField(name = "Color_Print")
        private String colorPrint;
    }

    // ========== 查询条件字段（非数据库字段）==========
    /** 创建开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createStartTime;

    /** 创建结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createEndTime;
}