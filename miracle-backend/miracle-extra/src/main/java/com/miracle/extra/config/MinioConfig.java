package com.miracle.extra.config;

import io.minio.MinioClient;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 配置类
 *
 * @author miracle
 */
@Getter
@Configuration
public class MinioConfig {

    @Value("${minio.endpoint}")
    public String endpoint;

    @Value("${minio.accessKey}")
    public String accessKey;

    @Value("${minio.secretKey}")
    public String secretKey;

    @Value("${minio.buckets.general}")
    public String generalBucket;

    @Value("${minio.buckets.material}")
    private String materialBucket;

    @Value("${minio.buckets.prod-order}")
    private String prodOrderBucket;

    @Value("${minio.buckets.avatar}")
    private String avatarBucket;

    /** 仅用于迁移期间读取 bucket_name 为空的历史图片；未配置时不启用回退。 */
    @Value("${minio.legacyBucket:}")
    public String legacyBucket;

    /** 历史说明：曾为仅物料部署关闭工单图片，现改为按业务归属路由。 */
    // 历史配置保留：@Value("${image-lifecycle.order-enabled:true}") public boolean orderImagesEnabled = true;
    // 新增：引用检查已按图片业务路由，工单功能不再受全局禁用开关影响。

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

}
