package com.miracle.extra.domain;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;
import java.util.List;

/** 新增：内外箱装箱的请求及查询模型，数量和料号始终由服务端解析。 */
public final class TycoPacking {
    private TycoPacking() { }
    @Data
    public static class Label {
        private String rawCode;
        private String identity;
        private String part;
        /** 从内箱原始条码解析的 SN 前缀，沿用 raw_code 保存原始标签。 */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private String snPrefix;
        private int quantity;
        private String traceNo;
        private String revision;
        private String salesOrder;
        private String productionBatch;
        private String boxSequence;
        private String dateCode;
    }
    @Data
    public static class Session extends Label {
        private String id;
        private String kind;
        private String status;
        private Long ownerId;
        private String ownerName;
        private int version;
        private Date expiresAt;
        private Date createdAt;
        private Date reviewedAt;
        private String reviewedBy;
        private List<String> products;
        private String boundInnerCode;
        /** 标签数量沿用 quantity，实际装箱量在完成时固定保存。 */
        private boolean tailBox;
        private Integer packedQuantity;
    }
    /** 新增：每次操作均携带独立幂等UUID和数据库版本；reviewed仅代表明确的人工确认。 */
    @Data
    public static class Command {
        private String operation;
        private String sessionId;
        private String kind;
        private String code;
        private Integer version;
        private Boolean reviewed;
        private String reason;
        /** 未传字段不参与旧请求的序列化，兼容历史幂等收据。 */
        @JsonInclude(JsonInclude.Include.NON_NULL)
        private Boolean tailBox;
    }

    /** 批量拆箱使用独立请求类型，保留旧操作的幂等指纹格式。 */
    @Data
    public static class BatchUnpackCommand extends Command {
        private List<UnpackTarget> targets;
    }

    /** 使用列表中选中时的版本，防止处理状态已变化的内箱。 */
    @Data
    public static class UnpackTarget {
        private String sessionId;
        private Integer version;
    }
    @Data
    public static class Receipt {
        private Long ownerId;
        private String fingerprint;
        private String sessionId;
    }
    /** 修复：返回数据库真实锁结果和事务上下文，避免把调用错误统一解释为繁忙。 */
    @Data
    public static class LockResult {
        private Integer returnCode;
        private Integer transactionCount;
        private Integer transactionState;
        private Integer sessionId;
        private String databaseName;
    }
}
