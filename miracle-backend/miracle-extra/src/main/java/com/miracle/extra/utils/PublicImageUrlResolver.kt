package com.miracle.extra.utils

import com.miracle.extra.config.MinioConfig
import com.miracle.extra.domain.ImageStore
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

/** 新增：由物料列表已关联的图片元数据生成 public 缩略图 URL，不查数据库、不访问 MinIO、不签名。 */
@Component
class PublicImageUrlResolver(private val config: MinioConfig) {
    fun thumbnailUrl(image: ImageStore?): String? {
        // 仅显示真实缩略图；缺图、待删除或误指原图时保持为空，不回退原图。
        if (image == null || image.status !in listOf(0, 2)) return null
        val key = image.thumbnailPath?.takeIf { it.isNotBlank() && it != image.imagePath } ?: return null
        val bucket = image.bucketName?.takeIf { it.isNotBlank() }
            ?: config.legacyBucket?.takeIf { it.isNotBlank() } ?: return null
        val endpoint = config.endpoint?.takeIf { it.isNotBlank() } ?: return null
        return try {
            // 分段编码空格、中文等字符，保留对象 key 目录；不携带签名和过期参数。
            UriComponentsBuilder.fromUriString(endpoint.trimEnd('/'))
                .pathSegment(bucket).pathSegment(*key.split('/').toTypedArray())
                .build().encode().toUriString()
        } catch (invalidLocation: IllegalArgumentException) {
            // 单条记录定位异常不阻断整页物料显示，前端继续提供主动查看入口。
            null
        }
    }
}
