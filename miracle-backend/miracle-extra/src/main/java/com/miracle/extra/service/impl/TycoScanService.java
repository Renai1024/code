package com.miracle.extra.service.impl;

import com.miracle.common.annotation.DataSource;
import com.miracle.common.enums.DataSourceType;
import com.miracle.common.utils.SecurityUtils;
import com.miracle.extra.domain.TycoScan;
import com.miracle.extra.mapper.TycoScanMapper;
import com.miracle.extra.utils.TycoScanRules;
import com.miracle.extra.service.ITycoScanService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;

/** 新增：后台重算QT和数量，箱头/全部产品码在同一数据库事务内原子提交。 */
@Service
@DataSource(DataSourceType.SLAVE2)
public class TycoScanService implements ITycoScanService {
    private final TycoScanMapper mapper;
    public TycoScanService(TycoScanMapper mapper) { this.mapper = mapper; }
    // 新增：按最新规则，所有未删除记录中的产品码和箱码分别全局唯一；旧箱内校验保存在历史快照。
    public boolean used(String code) {
        return mapper.count(TycoScanRules.product(code)) > 0;
    }
    public boolean boxUsed(String boxCode) {
        return mapper.countBox(TycoScanRules.parseBox(boxCode).boxCode()) > 0;
    }
    public List<TycoScan.Batch> list() { return mapper.list(); }
    public List<String> details(String id) { return mapper.details(id); }
    /** 新增：超时重试复用requestId；只允许同一用户、相同完整内容取回成功结果。 */
    public TycoScan.Batch replay(TycoScan.Submit request) {
        if (request == null || request.requestId() == null) throw new IllegalArgumentException("请求不能为空");
        try { UUID.fromString(request.requestId()); }
        catch (Exception e) { throw new IllegalArgumentException("无效提交标识"); }
        List<String> products = TycoScanRules.products(request);
        TycoScan.Batch saved = mapper.find(request.requestId());
        if (saved != null && (!SecurityUtils.getUserId().equals(saved.userId) ||
                !TycoScanRules.hash(TycoScanRules.parseBox(request.boxCode()).boxCode(), products).equals(saved.payloadHash)))
            throw new IllegalArgumentException("提交标识已使用，请核对提交记录");
        // 新增：删除保留请求墓碑；旧PDA/旧网络请求重试不能重新插入已删除业务记录。
        if (saved != null && "2".equals(saved.delFlag))
            throw new IllegalArgumentException("扫码记录已删除，请取消当前草稿后重新扫码");
        return saved;
    }
    @Transactional(rollbackFor = Exception.class)
    public TycoScan.Batch submit(TycoScan.Submit request) {
        TycoScan.Batch saved = replay(request);
        if (saved != null) return saved;
        // 新增：使用事务已绑定的同一连接，避免JDBC会话选项不满足筛选唯一索引的写入要求。
        mapper.prepareWriteSession();
        List<String> products = TycoScanRules.products(request);
        TycoScan.Box box = TycoScanRules.parseBox(request.boxCode());
        // 新增：幂等回放必须在唯一性检查前执行；预检后仍由数据库唯一索引处理并发竞争。
        if (boxUsed(box.boxCode())) throw new IllegalArgumentException("箱码已提交，请勿重复扫码");
        for (String code : products) if (used(code)) throw new IllegalArgumentException("产品码已提交：" + code);
        TycoScan.Batch batch = new TycoScan.Batch();
        batch.requestId = request.requestId(); batch.boxCode = box.boxCode(); batch.quantity = box.quantity();
        batch.userId = SecurityUtils.getUserId(); batch.userName = SecurityUtils.getUsername();
        batch.payloadHash = TycoScanRules.hash(box.boxCode(), products);
        if (mapper.insert(batch) != 1) throw new IllegalStateException("箱码保存失败");
        for (String code : products) if (mapper.insertProduct(batch.requestId, code) != 1)
            throw new IllegalStateException("产品码保存失败");
        return mapper.find(batch.requestId);
    }

    /** 新增：先按固定顺序锁定所有箱头，再删除明细和箱头，任一步异常均回滚。 */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByIds(String[] ids)
    {
        if (ids == null || ids.length == 0 || ids.length > 100)
            throw new IllegalArgumentException("每次请选择1至100条扫码记录");
        List<String> sorted;
        try {
            sorted = Arrays.stream(ids).map(id -> UUID.fromString(id).toString()).distinct().sorted().toList();
        } catch (Exception e) { throw new IllegalArgumentException("扫码记录编号无效"); }
        mapper.prepareWriteSession();
        // 新增：全部先校验，避免批量请求中途发现不存在的ID而部分删除。
        List<TycoScan.Batch> targets = sorted.stream().map(mapper::findForUpdate).toList();
        if (targets.stream().anyMatch(b -> b == null)) throw new IllegalArgumentException("部分扫码记录不存在，请刷新列表后重试");
        int removed = 0;
        for (TycoScan.Batch batch : targets) {
            if ("2".equals(batch.delFlag)) continue; // 新增：重复点击删除按幂等处理。
            if (mapper.deleteProducts(batch.requestId) != batch.quantity)
                throw new IllegalStateException("产品明细数量异常，删除已回滚");
            if (mapper.deleteBatch(batch.requestId) != 1)
                throw new IllegalStateException("箱码状态更新失败，删除已回滚");
            removed++;
        }
        return removed;
    }
}
