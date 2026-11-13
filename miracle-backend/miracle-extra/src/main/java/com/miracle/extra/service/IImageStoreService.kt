package com.miracle.extra.service;

import com.miracle.extra.domain.ImageStore
import com.miracle.extra.domain.enums.BucketType
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream


/**
 * 图片存储 Service 接口
 *
 * @author miracle
 */
 interface IImageStoreService {

    /**
     * 上传图片
     *
     * @param file 图片文件
     * @return 图片存储信息
     * @throws Exception 异常
     */

    @Throws(Exception::class)
    fun uploadImage(file: MultipartFile, businessType: BucketType): ImageStore

    /**
     * 下载图片
     *
     * @param imageId 图片ID
     * @return 图片输入流
     * @throws Exception 异常
     */
    @Throws(Exception::class)
    fun downloadImage(imageId: Long) : InputStream

    /**
     * 获取图片预览URL
     *
     * @param imageId 图片ID
     * @return 图片预览URL
     * @throws Exception 异常
     */
    @Throws(Exception::class)
    fun getImagePreviewUrl(imageId:  Long) : String

    /**
     * 删除图片
     *
     * @param imageId 图片ID
     * @return 结果
     * @throws Exception 异常
     */
    @Throws(Exception::class)
    fun deleteImage(imageId: Long) : Boolean

    /**
     * 查询图片存储列表
     *
     * @param imageStore 图片存储
     * @return 图片存储集合
     */
    @Throws(Exception::class)
    fun selectImageStoreList(imageStore: ImageStore): List<ImageStore>

    /**
     * 新增图片存储
     *
     * @param imageStore 图片存储
     * @return 结果
     */
    fun insertImageStore(imageStore: ImageStore): Int

    /**
     * 修改图片存储
     *
     * @param imageStore 图片存储
     * @return 结果
     */
    fun updateImageStore(imageStore: ImageStore): Int

    /**
     * 批量删除图片存储
     *
     * @param imageIds 需要删除的图片ID列表
     * @return 结果
     * @throws Exception 异常
     */
    @Throws(Exception::class)
    fun deleteImageStoreByIds(imageIds: Array<Long>) : Int

    /**
     * 根据图片ID查询图片存储
     *
     * @param imageId 图片ID
     * @return 图片存储
     */
    fun selectImageStoreById(imageId: Long): ImageStore

    /**
     * 根据图片路径查询图片存储
     *
     * @param imagePath 图片路径
     * @return 图片存储
     */
    fun selectImageStoreByPath(imagePath: String): ImageStore

    /** 新增：校验并预留本人暂存图片，跨库调用使用独立 SQL Server 事务。 */
    fun assertPendingOwned(imageId: Long, ownerUserId: Long)
    fun reservePending(imageId: Long, ownerUserId: Long, bindingKey: String)
    /** 新增：批量返回当前用户可读取的短期缩略图地址。 */
    fun thumbnailUrls(ids: List<Long>): Map<Long, String>
    /** 新增：供业务兼容 URL 列使用稳定地址，禁止落库短期签名。 */
    fun stableUrls(ids: List<Long>): List<String>

//    fun confirmImage(imageId: Long): Int
}