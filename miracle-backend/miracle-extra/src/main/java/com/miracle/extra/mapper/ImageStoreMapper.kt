package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType
import com.miracle.extra.domain.ImageStore
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;


/**
 * 图片存储 Mapper 接口
 *
 * @author miracle
 */
@Mapper
@DataSource(DataSourceType.SLAVE2)
interface ImageStoreMapper {

    /**
     * 查询图片存储列表
     *
     * @param imageStore 图片存储
     * @return 图片存储集合
     */
    fun selectImageStoreList(imageStore: ImageStore): List<ImageStore>;

    /**
     * 新增图片存储
     *
     * @param imageStore 图片存储
     * @return 结果
     */
    fun insertImageStore(imageStore: ImageStore): Int;

    /**
     * 修改图片存储
     *
     * @param imageStore 图片存储
     * @return 结果
     */
    fun updateImageStore(imageStore: ImageStore): Int;

    /**
     * 删除图片存储
     *
     * @param imageId 图片ID
     * @return 结果
     */
    fun deleteImageStoreById(imageId: Long): Int;

    /**
     * 批量删除图片存储
     *
     * @param imageIds 需要删除的图片ID列表
     * @return 结果
     */
    fun deleteImageStoreByIds(imageIds: Array<Long>): Int

    /**
     * 根据图片ID查询图片存储
     *
     * @param imageId 图片ID
     * @return 图片存储
     */
    fun selectImageStoreById(imageId:  Long): ImageStore

    /**
     * 根据图片路径查询图片存储
     *
     * @param imagePath 图片路径
     * @return 图片存储
     */
    fun selectImageStoreByPath(@Param("imagePath") imagePath:  String): ImageStore

    /**
     * 添加删除标记
     */
    fun markImageDeleting(@Param("imageId") imageId: Long): Int

    /** 新增：生命周期状态使用条件更新，避免清理和确认覆盖彼此。 */
    fun confirmPending(@Param("imageId") imageId: Long): Int
    fun reservePending(@Param("imageId") imageId: Long, @Param("owner") owner: Long,
                       @Param("binding") binding: String): Int
    fun expiredPending(): List<ImageStore>
    fun claimExpired(@Param("imageId") imageId: Long): Int
    /** 新增：取消与业务预留竞争时，仅可原子认领未预留的本人暂存图。 */
    fun claimCancel(@Param("imageId") imageId: Long, @Param("owner") owner: Long): Int
    fun deletingImages(): List<ImageStore>

//    fun confirmImageStore(imageId: Long): Int
}