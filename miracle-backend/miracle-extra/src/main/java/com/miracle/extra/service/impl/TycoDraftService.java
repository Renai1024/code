package com.miracle.extra.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.miracle.common.utils.SecurityUtils;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

/** 新增：账号级服务器草稿，Lua将版本比较、写入和24小时TTL作为一个原子操作。 */
@Service
public class TycoDraftService {
    private static final long TTL = 86400000L;
    // 新增：清理也写入带版本的空记录，阻止其他设备用旧版本复活已取消的草稿。
    static final String MUTATE = "local raw=redis.call('GET',KEYS[1]); " +
            "local old=raw and cjson.decode(raw) or {version=''}; " +
            "if old.version~=ARGV[1] then " +
            "if old.operation==ARGV[2] then return raw end; return '' end; " +
            "local t=redis.call('TIME'); local now=tonumber(t[1])*1000+math.floor(tonumber(t[2])/1000); " +
            "local value=cjson.encode({version=ARGV[2],operation=ARGV[2],expiresAt=now+tonumber(ARGV[4]),payload=ARGV[3]}); " +
            "redis.call('SET',KEYS[1],value,'PX',ARGV[4]); return value;";
    private final StringRedisTemplate redis;
    private final ObjectMapper json;
    public TycoDraftService(RedisConnectionFactory factory, ObjectMapper json) {
        // 新增：专用字符串序列化器，不改变项目既有RedisTemplate的JSON序列化配置。
        this.redis = new StringRedisTemplate(factory); this.json = json;
    }
    private String key() { return "tyco:scan:draft:v2:" + SecurityUtils.getUserId(); }
    public JsonNode read() {
        String raw = redis.opsForValue().get(key());
        if (raw == null) return json.createObjectNode().put("version", "").put("expiresAt", 0).putNull("data");
        return decode(raw);
    }
    public JsonNode save(JsonNode request) { validate(request.path("data")); return mutate(request, false); }
    public JsonNode clear(JsonNode request) { return mutate(request, true); }
    private JsonNode mutate(JsonNode request, boolean clear) {
        return mutateKey(key(),request,clear);
    }
    /** 新增：独立页签只保存未确认输入；产品清单和装箱状态必须以SQL Server为准。 */
    private String packingKey(String kind) {
        com.miracle.extra.utils.TycoPackingRules.kind(kind);
        return "tyco:packing:input:v1:" + SecurityUtils.getUserId() + ":" + kind;
    }
    public JsonNode packingRead(String kind) {
        String raw=redis.opsForValue().get(packingKey(kind));
        return raw==null ? json.createObjectNode().put("version","").put("expiresAt",0).putNull("data") : decode(raw);
    }
    public JsonNode packingSave(String kind,JsonNode request) {
        JsonNode data=request.path("data");
        if(!data.isObject() || !text(data,"boxInput",2048) || !text(data,"scanInput",2048) || !text(data,"sessionId",36) || data.toString().length()>6000)
            throw new IllegalArgumentException("输入草稿无效");
        return mutateKey(packingKey(kind),request,false);
    }
    private JsonNode mutateKey(String cacheKey,JsonNode request,boolean clear) {
        if (!request.path("version").isTextual() || request.path("version").asText().length() > 36)
            throw new IllegalArgumentException("草稿版本无效");
        String operation = request.path("operation").asText();
        try { UUID.fromString(operation); } catch (Exception e) { throw new IllegalArgumentException("草稿操作标识无效"); }
        String result = redis.execute(new DefaultRedisScript<>(MUTATE, String.class), List.of(cacheKey),
                request.path("version").asText(), operation, clear ? "null" : request.path("data").toString(), Long.toString(TTL));
        if (result == null || result.isEmpty()) throw new IllegalArgumentException("草稿版本冲突：其他设备已修改或清除草稿，请刷新核对");
        return decode(result);
    }
    private JsonNode decode(String raw) {
        // 新增：Lua只处理版本及JSON字符串，不解码业务数组，避免空products数组被cjson重新编码为对象。
        try {
            ObjectNode result = (ObjectNode) json.readTree(raw);
            result.set("data", json.readTree(result.path("payload").asText("null")));
            result.remove("payload"); return result;
        }
        catch (Exception e) { throw new IllegalStateException("服务器草稿读取失败", e); }
    }
    /** 新增：草稿允许尚未完成的扫码，但限制结构和大小；最终提交仍执行完整业务校验。 */
    private void validate(JsonNode data) {
        if (!data.isObject() || data.toString().length() > 1500000 ||
                !data.path("requestId").isTextual() || !data.path("uncertain").isBoolean() ||
                !data.path("quantity").isIntegralNumber() || data.path("quantity").asLong() < 0 || data.path("quantity").asLong() > 5000 ||
                !text(data, "boxInput", 2048) || !text(data, "box", 2048) || !text(data, "productInput", 256) ||
                !data.path("products").isArray() || data.path("products").size() > 5000)
            throw new IllegalArgumentException("草稿格式或大小不正确");
        try { UUID.fromString(data.path("requestId").asText()); }
        catch (Exception e) { throw new IllegalArgumentException("草稿请求标识无效"); }
        for (JsonNode code : data.path("products")) if (!code.isTextual() || code.asText().length() > 256)
            throw new IllegalArgumentException("草稿产品码格式不正确");
    }
    private boolean text(JsonNode data, String field, int max) { return data.path(field).isTextual() && data.path(field).asText().length() <= max; }
}
