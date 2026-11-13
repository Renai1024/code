package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miracle.common.annotation.Excel;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class MesApiDataRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String dataTrackId;

    @Excel(name = "设备编号")
    private String deviceId;

    @Excel(name = "设备类型")
    private String deviceType;

    @Excel(name = "产品型号")
    private String productModel;

    @Excel(name = "工单")
    private String workOrder;

    @Excel(name = "工作结果")
    private String workResults;

    @Excel(name = "缺陷原因")
    private String defectReason;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "工作时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date workDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "创建时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @Excel(name = "条码列表", width = 50)
    private String barCodeListStr;

    @Excel(name = "物料批次", width = 50)
    private String materialBatchListStr;

    @Excel(name = "物料条码", width = 50)
    private String materialBarCodeListStr;

    @Excel(name = "数据详情", width = 100)
    private String dataInfoListStr;

    private List<MesApiDataBarCodeDetail> barCodeList;

    private List<MesApiDataMaterialBatchDetail> materialBatchList;

    private List<MesApiDataMaterialBarCodeDetail> materialBarCodeList;

    private List<MesApiDataInfoDetail> dataInfoList;

    @Data
    public static class MesApiDataBarCodeDetail {
        private Long id;
        private String dataTrackId;
        private String barCode;
    }

    @Data
    public static class MesApiDataMaterialBatchDetail {
        private Long id;
        private String dataTrackId;
        private String materialBatch;
        private String materialModel;
    }

    @Data
    public static class MesApiDataMaterialBarCodeDetail {
        private Long id;
        private String dataTrackId;
        private String materialBarCode;
    }

    @Data
    public static class MesApiDataInfoDetail {
        private Long id;
        private String dataTrackId;
        private Integer nodeIndex;
        private String dataNo;
        private String dataName;
        private String dataValue;
    }

    private String searchBarCode;
    private String searchMaterialBatch;
    private String searchMaterialBarCode;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date workDateStart;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date workDateEnd;
}