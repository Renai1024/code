package com.miracle.extra.domain;

import com.miracle.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 图片存储实体
 *
 * @author miracle
 */
@Data
@EqualsAndHashCode(callSuper = true)
data class ImageStore(

    var serialVersionUID: Long = 1L,

    /** 图片ID **/
    var imageId: Long? = null,

    /** 图片名称 **/
    var imageName: String? = null,

    /** 图片原始名称 **/
    var originalName: String? = null,

    /** 图片存储路径 **/
    var imagePath: String? = null,

    /** 缩略图对象 key；签名地址只在响应时生成。 **/
    var thumbnailPath: String? = null,
    /** 暂存上传者，用于提交及预览授权。 **/
    var ownerUserId: Long? = null,
    /** 暂存有效期；确认后清空。 **/
    var expiresAt: java.util.Date? = null,
    /** 单业务归属预留，阻止同一暂存图片被多个业务绑定。 **/
    var bindingKey: String? = null,

    /** 图片url **/
    var imageUrl: String? = null,

    /** 图片大小(字节) **/
    var imageSize: Long? = null,

    /** 图片类型 **/
    var imageType: String? = null,

    /** 存储桶名称 **/
    var bucketName: String? = null,

    /** 图片状态（0：已确认，1：待删除，2：待业务确认）**/
    var status: Int? = null,

) : BaseEntity(), Serializable