package com.miracle.extra.domain;

import java.util.List;

/** 新增：泰科扫码独立传输模型，客户料号首版不解析、不由客户端伪造数量。 */
public final class TycoScan {
    private TycoScan() {}
    public record Box(String boxCode, int quantity) {}
    public record Submit(String requestId, String boxCode, List<String> productCodes) {}
    /** 新增：公共字段供 Jackson/MyBatis 映射，时间按数据库实际记录返回。 */
    public static class Batch {
        public String requestId;
        public String boxCode;
        public Integer quantity;
        public Long userId;
        public String userName;
        public String payloadHash;
        public java.util.Date createdAt;
        /** 新增：0正常、2已删除；删除记录保留用于阻止旧请求重新提交。 */
        public String delFlag;
    }
}
