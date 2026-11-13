package com.miracle.extra.mapper;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.extra.domain.TycoPacking.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/** 新增：内外箱独立表，不覆盖旧版箱码和产品记录；复用已确认的SLAVE2。 */
@Mapper
@DataSource(DataSourceType.SLAVE2)
public interface TycoPackingMapper {
    void prepare();
    // 修复：旧 void 签名见 implementation-history/20261103-tyco-lock-fix；锁结果由服务判断。
    LockResult lockUser(@Param("owner") Long owner);
    LockResult lockCodes();
    List<Session> expired();
    Session find(@Param("id") String id);
    Session current(@Param("owner") Long owner, @Param("kind") String kind);
    Session activeBox(@Param("kind") String kind,@Param("identity") String identity);
    Session previousBox(@Param("kind") String kind,@Param("identity") String identity);
    Session snOwner(@Param("code") String code);
    int legacySn(@Param("code") String code);
    int legacyBox(@Param("code") String code);
    List<String> legacyCandidates(@Param("identity") String identity);
    void insert(Session session);
    int renew(@Param("id") String id);
    int addSn(@Param("id") String id,@Param("code") String code);
    int removeSn(@Param("id") String id,@Param("code") String code);
    int releaseSn(@Param("id") String id);
    List<String> products(@Param("id") String id);
    int status(@Param("id") String id,@Param("status") String status,@Param("active") boolean active,@Param("draft") boolean draft);
    // 原签名保存在 implementation-history/20261105-tyco-tail-box。
    int finish(@Param("id") String id,@Param("user") String user,
               @Param("tailBox") boolean tailBox,@Param("packedQuantity") int packedQuantity);
    void bind(@Param("id") String id,@Param("outerId") String outerId,@Param("innerId") String innerId);
    String outerForInner(@Param("id") String id);
    String innerForOuter(@Param("id") String id);
    int unbind(@Param("id") String id);
    List<Session> list(@Param("kind") String kind);
    // 用户端详情使用有效范围，业务操作仍用 find 读取历史状态以校验和重试。
    Session findVisible(@Param("id") String id);
    Receipt receipt(@Param("operation") String operation);
    void receiptSave(@Param("operation") String operation,@Param("owner") Long owner,@Param("fingerprint") String fingerprint,@Param("sessionId") String sessionId);
    void audit(@Param("id") String id,@Param("action") String action,@Param("detail") String detail,@Param("user") String user);
}
