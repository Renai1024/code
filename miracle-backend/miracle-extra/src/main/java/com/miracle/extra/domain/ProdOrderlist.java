package com.miracle.extra.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 生产工单实体类
 */
public class ProdOrderlist {

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

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 修改时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date modifyTime;

    /** 创建人*/
    private String createBy;
    /** 修改人*/
    private String modifyBy;



    /**
     * 图片
     * 查询时不返回，新增时由前端传入
     */
    private String image;

    /** 新增：持久化图片关联，按列表顺序返回；null 表示局部更新时保留原关联。 */
    private java.util.List<Long> imageIds;
    public java.util.List<Long> getImageIds() { return imageIds; }
    public void setImageIds(java.util.List<Long> imageIds) { this.imageIds = imageIds; }

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getModifyBy() {
        return modifyBy;
    }

    public void setModifyBy(String modifyBy) {
        this.modifyBy = modifyBy;
    }
}