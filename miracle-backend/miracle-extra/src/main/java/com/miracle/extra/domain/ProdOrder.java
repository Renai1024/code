package com.miracle.extra.domain;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 生产工单实体类
 */
public class ProdOrder {
    /** 生产工单号 */
    private String productionOrderNo;
    
    /** 车间 */
    private String workshop;
    
    /** 物料编号 */
    private String materialCode;
    
    /** 物料名称 */
    private String materialName;
    
    /** 计划数量 */
    private BigDecimal plannedQuantity;
    
    /** 创建时间（查询时不返回） */
    private Date createTime;
    
    /** 图片（查询时不返回） */
    private byte[] image;

    // ========== Getter 和 Setter ==========
    
    public String getProductionOrderNo() {
        return productionOrderNo;
    }

    public void setProductionOrderNo(String productionOrderNo) {
        this.productionOrderNo = productionOrderNo;
    }

    public String getWorkshop() {
        return workshop;
    }

    public void setWorkshop(String workshop) {
        this.workshop = workshop;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public BigDecimal getPlannedQuantity() {
        return plannedQuantity;
    }

    public void setPlannedQuantity(BigDecimal plannedQuantity) {
        this.plannedQuantity = plannedQuantity;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}