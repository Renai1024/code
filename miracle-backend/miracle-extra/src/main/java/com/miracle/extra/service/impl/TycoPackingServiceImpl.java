package com.miracle.extra.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.common.utils.SecurityUtils;
import com.miracle.extra.domain.TycoPacking.*;
import com.miracle.extra.mapper.TycoPackingMapper;
import com.miracle.extra.service.ITycoPackingService;
import com.miracle.extra.utils.TycoPackingRules;
import com.miracle.extra.utils.TycoScanRules;
import org.springframework.beans.BeanUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/** 新增：逐条SN占用、人工核对完成、内外箱一对一绑定，保留历史任务及操作墓碑。 */
@Service
@DataSource(DataSourceType.SLAVE2)
public class TycoPackingServiceImpl implements ITycoPackingService {
    private static final Logger log=LoggerFactory.getLogger(TycoPackingServiceImpl.class);
    private final TycoPackingMapper mapper;
    private final ObjectMapper json;
    public TycoPackingServiceImpl(TycoPackingMapper mapper,ObjectMapper json) { this.mapper=mapper; this.json=json; }
    // 历史实现见 implementation-history/20261103-tyco-lock-fix：mapper.prepare(); mapper.lockCodes();
    private void lock() { prepareTransaction(); checkLock(mapper.lockCodes(),"装箱操作"); }
    /** 修复：只有 Spring 管理的可写事务才能启动数据库事务，防止绕过代理后泄漏事务锁。 */
    private void prepareTransaction() {
        if(!TransactionSynchronizationManager.isActualTransactionActive()
                || TransactionSynchronizationManager.isCurrentTransactionReadOnly())
            throw new IllegalStateException("装箱操作缺少可写事务，请检查服务事务配置");
        mapper.prepare();
    }
    /** 修复：保留返回码及数据库事务上下文；失败抛出异常，让 Spring 回滚并释放已取得的锁。 */
    private void checkLock(LockResult result,String scope) {
        if(result==null || result.getReturnCode()==null || result.getTransactionCount()==null
                || result.getTransactionCount()<1 || !Integer.valueOf(1).equals(result.getTransactionState())) {
            log.error("泰科装箱锁事务异常 scope={}, result={}",scope,result);
            throw new IllegalStateException(scope+"锁事务状态异常，请检查后台日志");
        }
        int code=result.getReturnCode();
        if(code==0 || code==1) return;
        log.error("泰科装箱获取锁失败 scope={}, result={}",scope,result);
        String cause=switch(code) {
            case -1 -> "等待锁超时，请稍后重试";
            case -2 -> "锁请求被取消，请重试";
            case -3 -> "发生数据库死锁，本次操作已回滚，请重试";
            case -999 -> "锁调用失败，请检查数据库事务、参数及权限";
            default -> "获取锁失败，请检查后台日志";
        };
        throw new IllegalArgumentException(scope+cause+"（返回码："+code+"）");
    }
    private void expire() {
        for (Session s : mapper.expired()) {
            mapper.releaseSn(s.getId()); mapper.status(s.getId(),"EXPIRED",false,false);
            mapper.audit(s.getId(),"EXPIRE","24小时未完成，释放占用","系统");
        }
    }
    @Override @Transactional(rollbackFor=Exception.class)
    @Scheduled(fixedDelay=1800000,initialDelay=1800000)
    public void expireDrafts() { lock(); expire(); }
    @Override @Transactional(rollbackFor=Exception.class)
    public Session current(String kind) {
        TycoPackingRules.kind(kind); lock(); expire();
        return enrich(mapper.current(SecurityUtils.getUserId(),kind));
    }
    @Override public List<Session> list(String kind) { return mapper.list(TycoPackingRules.kind(kind)); }
    @Override public Session detail(String id) { return enrich(mapper.findVisible(uuid(id))); }
    private Session enrich(Session s) {
        if (s == null) return null;
        s.setProducts(mapper.products(s.getId()));
        if ("INNER".equals(s.getKind())) {
            s.setSnPrefix(TycoPackingRules.storedSnPrefix(s));
        } else if ("OUTER".equals(s.getKind())) {
            String inner=mapper.innerForOuter(s.getId());
            if (inner!=null) s.setBoundInnerCode(mapper.find(inner).getRawCode());
        }
        return s;
    }
    private String uuid(String id) {
        try { if (id==null || !UUID.fromString(id).toString().equalsIgnoreCase(id)) throw new IllegalArgumentException(); return id; }
        catch(Exception e) { throw new IllegalArgumentException("操作或任务编号无效"); }
    }
    private void version(Session s,Integer expectedVersion) {
        if (expectedVersion==null || expectedVersion!=s.getVersion()) throw new IllegalArgumentException("任务已被其他设备修改，请刷新任务");
    }
    private Session owned(Command c) {
        Session s=mapper.find(uuid(c.getSessionId()));
        if (s==null || !SecurityUtils.getUserId().equals(s.getOwnerId())) throw new IllegalArgumentException("装箱任务不存在或不属于当前账号");
        version(s,c.getVersion()); return s;
    }
    private void scanning(Session s) {
        if (!"SCANNING".equals(s.getStatus())) throw new IllegalArgumentException("当前任务已完成、取消或过期，请刷新任务");
    }
    /** 新增：操作收据、SN占用和状态在同一事务，网络超时复用operation安全重试。 */
    @Override @Transactional(rollbackFor=Exception.class)
    public Session execute(String action,Command c) {
        if (c==null) throw new IllegalArgumentException("请求不能为空");
        uuid(c.getOperation());
        if((c.getCode()!=null && c.getCode().length()>2048) || (c.getReason()!=null && c.getReason().length()>500))
            throw new IllegalArgumentException("请求字段过长，箱码最多2048字符，原因最多500字");
        String fingerprint;
        try { fingerprint=TycoScanRules.hash(action,List.of(json.writeValueAsString(c))); }
        catch(Exception e) { throw new IllegalArgumentException("请求格式无效"); }
        Long user=SecurityUtils.getUserId();
        // 修复：依次校验账号锁和全局锁，任何一次失败都不得继续读写业务数据。
        prepareTransaction(); checkLock(mapper.lockUser(user),"当前账号"); checkLock(mapper.lockCodes(),"装箱操作"); expire();
        Receipt prior=mapper.receipt(c.getOperation());
        if(prior!=null) {
            if(!user.equals(prior.getOwnerId()) || !fingerprint.equals(prior.getFingerprint())) throw new IllegalArgumentException("操作编号已被其他请求使用");
            return enrich(mapper.find(prior.getSessionId()));
        }
        if ("unpack-batch".equals(action) && c instanceof BatchUnpackCommand batch) {
            return unpackBatch(batch,user,fingerprint);
        }
        Session s;
        if ("start".equals(action)) {
            Label label=TycoPackingRules.parseForStart(c.getKind(),c.getCode());
            if (mapper.current(user,c.getKind())!=null) throw new IllegalArgumentException("此页签已有未完成任务，请恢复或取消后再开始");
            Session used=mapper.activeBox(c.getKind(),label.getIdentity());
            if(used!=null) throw new IllegalArgumentException(("INNER".equals(c.getKind())?"内箱码":"外箱码")+"已使用或正在装箱");
            if(mapper.legacyBox(label.getRawCode())>0) throw new IllegalArgumentException("此箱码属于旧版有效记录，请先核验历史数据");
            for(String raw:mapper.legacyCandidates(label.getIdentity())) {
                Label old=null;
                try {
                    old = "INNER".equals(c.getKind()) ? TycoPackingRules.parseInnerForPart(raw, label.getPart())
                            : TycoPackingRules.parse(c.getKind(), raw);
                } catch(IllegalArgumentException ignored) { /* 历史未知格式不猜测分类。 */ }
                if(old!=null && old.getIdentity().equals(label.getIdentity()))
                    throw new IllegalArgumentException("此箱码标识属于旧版有效记录，请先核验历史数据");
            }
            // 历史校验保留：箱标识本身唯一，无需再用已释放任务的数量等标签字段限制新周期。
            // Session previous=mapper.previousBox(c.getKind(),label.getIdentity());
            // if(previous!=null && !sameLabel(previous,label)) throw new IllegalArgumentException("同一箱标识的料号、数量或标签信息发生变化，请核对");
            // 优化：上面的 activeBox 继续阻止有效占用；取消/过期/解绑后的新任务独立保存标签，历史不覆盖。
            // sameLabel 仍用于实际绑定内箱时核对完整标签，避免绑定数量不一致的内箱。
            s=new Session(); BeanUtils.copyProperties(label,s);
            s.setId(UUID.randomUUID().toString()); s.setKind(c.getKind()); s.setOwnerId(user); s.setOwnerName(SecurityUtils.getUsername());
            mapper.insert(s);
        } else if ("unpack".equals(action) || "unbind".equals(action)) {
            s=mapper.find(uuid(c.getSessionId()));
            if(s==null) throw new IllegalArgumentException("记录不存在");
            // 历史 reason(c) 校验见备份。拆箱、解绑均通过前端确认，继续核对版本和状态。
            version(s,c.getVersion());
            if ("unbind".equals(action)) {
                if(!"OUTER".equals(s.getKind()) || !"BOUND".equals(s.getStatus())) throw new IllegalArgumentException("外箱没有有效关联");
                String inner=mapper.innerForOuter(s.getId());
                if(inner==null || mapper.unbind(s.getId())!=1) throw new IllegalArgumentException("关联状态异常");
                mapper.status(inner,"PACKED",true,false); mapper.status(s.getId(),"UNBOUND",false,false);
                // 两侧审计均提供非空描述，兼容旧客户端已填写的原因。
                String unbindDetail = c.getReason()==null || c.getReason().isBlank() ? "确认解绑" : c.getReason();
                mapper.audit(inner,"OUTER_UNBIND",unbindDetail,SecurityUtils.getUsername());
            } else {
                checkUnpackable(s);
                unpack(s);
            }
        } else {
            s=owned(c); scanning(s);
            switch(action) {
                case "scan", "remove" -> {
                    if(!"INNER".equals(s.getKind())) throw new IllegalArgumentException("外箱任务不能扫描产品SN");
                    String code=TycoScanRules.product(c.getCode());
                    if("remove".equals(action)) {
                        if(mapper.removeSn(s.getId(),code)!=1) throw new IllegalArgumentException("产品SN不在当前任务中");
                    } else {
                        // 以数据库中本箱原始标签为准，前缀不符时不占用 SN，也不续期。
                        String snPrefix = TycoPackingRules.storedSnPrefix(s);
                        if (snPrefix != null && !code.startsWith(snPrefix))
                            throw new IllegalArgumentException("扫描的SN防呆码错误");
                        Session used=mapper.snOwner(code);
                        if(used!=null) throw new IllegalArgumentException("SCANNING".equals(used.getStatus())?"产品SN码已被扫描":"产品SN码已在其他箱完成装箱");
                        if(mapper.legacySn(code)>0) throw new IllegalArgumentException("产品SN码已在旧版记录完成装箱");
                        if(mapper.products(s.getId()).size()>=s.getQuantity()) throw new IllegalArgumentException("内箱已扫满，请完成装箱");
                        mapper.addSn(s.getId(),code);
                    }
                    mapper.renew(s.getId());
                }
                case "finish" -> {
                    // 优化：reviewed 表示核对弹窗中主动点击确认，不再表示勾选复选框；后台仍要求明确确认。
                    if(!"INNER".equals(s.getKind()) || !Boolean.TRUE.equals(c.getReviewed())) throw new IllegalArgumentException("请核对实物、客户料号和数量后点击确认装箱");
                    int packedQuantity = mapper.products(s.getId()).size();
                    boolean tailBox = Boolean.TRUE.equals(c.getTailBox());
                    // 尾箱至少扫描一件，所有箱型都不能超过标签数量。
                    if (packedQuantity < 1 || packedQuantity > s.getQuantity())
                        throw new IllegalArgumentException("产品SN数量须为1至标签数量");
                    if (!tailBox && packedQuantity != s.getQuantity())
                        throw new IllegalArgumentException("产品SN数量必须与标签数量一致，未满箱请勾选是否尾箱");
                    if(mapper.finish(s.getId(),SecurityUtils.getUsername(),tailBox,packedQuantity)!=1) throw new IllegalArgumentException("内箱状态已变化");
                }
                case "bind" -> {
                    if(!"OUTER".equals(s.getKind())) throw new IllegalArgumentException("请先扫描外箱二维码");
                    Label label=TycoPackingRules.parseInnerForPart(c.getCode(), s.getPart());
                    Session inner=mapper.activeBox("INNER",label.getIdentity());
                    if(inner==null || !"PACKED".equals(inner.getStatus()) || mapper.outerForInner(inner.getId())!=null)
                        throw new IllegalArgumentException("扫描内箱码不符：未完成装箱或已关联其他外箱");
                    // 历史数量校验保存在 implementation-history/20261105-tyco-tail-material-only。
                    // 按数据库中完成装箱时保存的尾箱标记判断，关联请求不能自行声明尾箱绕过校验。
                    if (!s.getPart().equals(inner.getPart()))
                        throw new IllegalArgumentException("扫描内箱码不符：客户料号不一致");
                    if (!inner.isTailBox()) {
                        // 普通箱仍核对标签、已确认数量与有效 SN 数，尾箱关联仅比较客户料号。
                        int innerQuantity = inner.getPackedQuantity() == null ? inner.getQuantity() : inner.getPackedQuantity();
                        if (!sameLabel(inner,label) || s.getQuantity()!=innerQuantity || mapper.products(inner.getId()).size()!=innerQuantity)
                            throw new IllegalArgumentException("扫描内箱码不符：装箱数量不一致");
                    }
                    mapper.bind(UUID.randomUUID().toString(),s.getId(),inner.getId());
                    mapper.status(inner.getId(),"BOUND",true,false); mapper.status(s.getId(),"BOUND",true,false);
                    mapper.audit(inner.getId(),"OUTER_BIND",s.getId(),SecurityUtils.getUsername());
                }
                case "cancel" -> { mapper.releaseSn(s.getId()); mapper.status(s.getId(),"CANCELLED",false,false); }
                default -> throw new IllegalArgumentException("操作类型无效");
            }
        }
        mapper.receiptSave(c.getOperation(),user,fingerprint,s.getId());
        // 新增：扫码审计固定保存实际条码，不能被额外传入的reason覆盖。
        // 优化：无原因拆箱仍写入非空审计明细；保留旧客户端已填写的拆箱原因。
        // 历史：String detail=("unbind".equals(action)||"unpack".equals(action))?c.getReason():Objects.toString(c.getCode(),"");
        String detail=switch(action) {
            case "unbind" -> c.getReason()==null || c.getReason().isBlank() ? "确认解绑" : c.getReason();
            case "unpack" -> c.getReason()==null || c.getReason().isBlank() ? "确认拆箱" : c.getReason();
            default -> Objects.toString(c.getCode(),"");
        };
        mapper.audit(s.getId(),action,detail,SecurityUtils.getUsername());
        return enrich(mapper.find(s.getId()));
    }

    /** 在已有事务和装箱锁下先校验整批，再释放占用并逐箱写审计。 */
    private Session unpackBatch(BatchUnpackCommand command, Long user, String fingerprint) {
        List<UnpackTarget> targets = command.getTargets();
        if (targets == null || targets.isEmpty() || targets.size() > 100) {
            throw new IllegalArgumentException("请求每次须选择1至100个内箱");
        }

        Set<String> ids = new HashSet<>();
        List<Session> boxes = new ArrayList<>();
        for (UnpackTarget target : targets) {
            if (target == null) throw new IllegalArgumentException("请求包含空的内箱记录");
            String id = uuid(target.getSessionId()).toLowerCase(Locale.ROOT);
            if (!ids.add(id)) throw new IllegalArgumentException("请求包含重复的内箱记录");
            Session box = mapper.find(id);
            if (box == null) throw new IllegalArgumentException("请求中的内箱记录不存在，请刷新列表");
            version(box,target.getVersion());
            checkUnpackable(box);
            boxes.add(box);
        }

        String operator = SecurityUtils.getUsername();
        for (Session box : boxes) {
            unpack(box);
            mapper.audit(box.getId(),"unpack","批量拆箱",operator);
        }
        // 整批共用一张收据，以首个内箱作为重试时的返回记录。
        String firstId = boxes.get(0).getId();
        mapper.receiptSave(command.getOperation(),user,fingerprint,firstId);
        return enrich(mapper.find(firstId));
    }

    /** 单箱和批量拆箱共用状态与关联校验。 */
    private void checkUnpackable(Session box) {
        if (!"INNER".equals(box.getKind()) || !"PACKED".equals(box.getStatus()) || mapper.outerForInner(box.getId()) != null) {
            throw new IllegalArgumentException("内箱未装箱或已关联外箱，请先解除外箱关联");
        }
    }

    /** 释放 SN 后更新箱状态，失败时让事务回滚。 */
    private void unpack(Session box) {
        mapper.releaseSn(box.getId());
        if (mapper.status(box.getId(),"UNBOUND",false,false) != 1) {
            throw new IllegalArgumentException("内箱状态更新失败，请刷新列表");
        }
    }
    // 原必填原因校验已保存至 implementation-history/20261106-tyco-unbind-confirm。
    private boolean sameLabel(Label a,Label b) {
        return Objects.equals(a.getPart(),b.getPart()) && a.getQuantity()==b.getQuantity() &&
            Objects.equals(a.getRevision(),b.getRevision()) && Objects.equals(a.getSalesOrder(),b.getSalesOrder()) &&
            Objects.equals(a.getProductionBatch(),b.getProductionBatch()) && Objects.equals(a.getBoxSequence(),b.getBoxSequence()) && Objects.equals(a.getDateCode(),b.getDateCode());
    }
}
