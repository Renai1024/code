package com.miracle.extra.resolver;

import com.miracle.extra.config.MinioConfig;
import com.miracle.extra.domain.enums.BucketType;
import org.springframework.stereotype.Component;

@Component
public class MinioBucketResolver {
    private final MinioConfig minioConfig;

    public MinioBucketResolver(MinioConfig minioConfig) {
        this.minioConfig = minioConfig;
    }

    public String resolve(BucketType type) {
        switch (type) {
            case MATERIAL:
                return minioConfig.getMaterialBucket();
            case PROD_ORDER:
                return minioConfig.getProdOrderBucket();
            case AVATAR:
                return minioConfig.getAvatarBucket();
            case GENERAL:
            default:
                return minioConfig.generalBucket;
        }
    }
}
