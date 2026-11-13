package com.miracle.extra.service.impl;

import com.miracle.common.utils.SecurityUtils;
import com.miracle.extra.domain.ProdOrderlist;
import com.miracle.extra.mapper.ProdOrderlistMapper;
import com.miracle.extra.service.ProdOrderlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 生产工单 Service 实现类
 */
@Service
@com.miracle.common.annotation.DataSource(com.miracle.common.enums.DataSourceType.MASTER)
public class ProdOrderlistServiceImpl implements ProdOrderlistService {

    @Autowired
    private ProdOrderlistMapper prodOrderlistMapper;

    /** 新增：工单关联、图片校验与本库 outbox 协作。 */
    @Autowired private com.miracle.extra.mapper.ProdOrderImageMapper orderImages;
    @Autowired private OrderImageOutbox orderOutbox;
    @Autowired private com.miracle.extra.service.IImageStoreService images;

    // 历史实现保留：2026-10-14 改为业务事务与 outbox 驱动。
    //     @Override
    //     public ProdOrderlist getByOrderNo(String productionOrderNo) {
    //         if (!StringUtils.hasText(productionOrderNo)) {
    //             return null;
    //         }
    //         return prodOrderlistMapper.selectByOrderNo(productionOrderNo);
    //     }
    @Override
    public ProdOrderlist getByOrderNo(String no) {
        if (!StringUtils.hasText(no)) return null;
        ProdOrderlist row = prodOrderlistMapper.selectByOrderNo(no);
        if (row != null) row.setImageIds(orderImages.selectImageIds(no));
        return row;
    }

    // 历史实现保留：2026-10-14 改为业务事务与 outbox 驱动。
    //     @Override
    //     public List<ProdOrderlist> list(String productionOrderNo, String materialCode, String materialName) {
    //         return prodOrderlistMapper.selectList(productionOrderNo, materialCode, materialName);
    //     }
    /** 新增：一次查询本页关联，避免逐行 N+1 读取并保留分页对象。 */
    @Override
    public List<ProdOrderlist> list(String no, String code, String name) {
        List<ProdOrderlist> rows = prodOrderlistMapper.selectList(no, code, name);
        if (rows.isEmpty()) return rows;
        java.util.Map<String, List<Long>> ids = new java.util.HashMap<>();
        for (ProdOrderlist link : orderImages.selectForOrders(rows.stream().map(ProdOrderlist::getProductionOrderNo).collect(java.util.stream.Collectors.toList())))
            ids.computeIfAbsent(link.getProductionOrderNo(), key -> new java.util.ArrayList<>()).add(Long.valueOf(link.getImage()));
        for (ProdOrderlist row : rows) row.setImageIds(ids.getOrDefault(row.getProductionOrderNo(), java.util.Collections.emptyList()));
        return rows;
    }

    @Override
    public Long count(String productionOrderNo, String materialCode, String materialName) {
        return prodOrderlistMapper.count(productionOrderNo, materialCode, materialName);
    }

    // 历史实现保留：2026-10-14 改为业务事务与 outbox 驱动。
    //     @Override
    //     public int insert(ProdOrderlist prodOrder) {
    //         if (prodOrder == null || !StringUtils.hasText(prodOrder.getProductionOrderNo())) {
    //             return 0;
    //         }
    //         prodOrder.setCreateBy(SecurityUtils.getUsername());
    //         prodOrder.setCreateTime(new Date());
    //         return prodOrderlistMapper.insert(prodOrder);
    //     }
    /** 新增：新增业务行、关联和确认事件属于同一 MySQL 事务。 */
    @Override @org.springframework.transaction.annotation.Transactional
    public int insert(ProdOrderlist input) {
        requireOrder(input);
        List<Long> ids = normalized(input.getImageIds());
        reserve(input.getProductionOrderNo(), ids, java.util.Collections.emptyList());
        if (!ids.isEmpty()) input.setImage(String.join(",", images.stableUrls(ids)));
        input.setCreateBy(SecurityUtils.getUsername()); input.setCreateTime(new Date());
        if (prodOrderlistMapper.insert(input) != 1) throw new IllegalStateException("工单新增失败");
        writeLinks(input.getProductionOrderNo(), ids);
        for (Long id : ids) orderOutbox.enqueueConfirm(id);
        return 1;
    }

    // 历史实现保留：2026-10-14 改为业务事务与 outbox 驱动。
    //     @Override
    //     public int insertBatch(List<ProdOrderlist> list) {
    //         if (list == null || list.isEmpty()) {
    //             return 0;
    //         }
    //         for (ProdOrderlist item : list) {
    //             item.setCreateBy(SecurityUtils.getUsername());
    //             item.setCreateTime(new Date());
    //         }
    //         return prodOrderlistMapper.insertBatch(list);
    //     }
    /** 新增：批量事务统一覆盖所有业务行、关联与 outbox，失败整批回滚。 */
    @Override @org.springframework.transaction.annotation.Transactional
    public int insertBatch(List<ProdOrderlist> inputs) {
        if (inputs == null || inputs.isEmpty()) return 0;
        int total = 0;
        for (ProdOrderlist item : inputs) total += insert(item);
        return total;
    }

    // 历史实现保留：2026-10-14 改为业务事务与 outbox 驱动。
    //     @Override
    //     public int update(ProdOrderlist prodOrder) {
    //         if (prodOrder == null || !StringUtils.hasText(prodOrder.getProductionOrderNo())) {
    //             return 0;
    //         }
    //         prodOrder.setModifyBy(SecurityUtils.getUsername());
    //         prodOrder.setModifyTime(new Date());
    //         return prodOrderlistMapper.update(prodOrder);
    //     }
    /** 新增：锁定工单后比较旧关联，null 保留旧 ID，空数组显式清空图片。 */
    @Override @org.springframework.transaction.annotation.Transactional
    public int update(ProdOrderlist input) {
        requireOrder(input);
        String no = input.getProductionOrderNo();
        if (orderImages.lockOrder(no) == null) throw new IllegalArgumentException("工单不存在");
        List<Long> old = orderImages.selectImageIds(no);
        List<Long> ids = input.getImageIds() == null ? old : normalized(input.getImageIds());
        reserve(no, ids, old);
        // 无关联历史记录保留原 URL；明确提交图片集合时才更新兼容列。
        if (input.getImageIds() != null) {
            // 新增：未迁移工单保留客户端仍选中的历史 URL，只接受原记录已有值。
            java.util.LinkedHashSet<String> urls = new java.util.LinkedHashSet<>(images.stableUrls(ids));
            if (old.isEmpty()) {
                String saved = prodOrderlistMapper.selectByOrderNo(no).getImage();
                if (saved != null && input.getImage() != null) {
                    List<String> requested = java.util.Arrays.asList(input.getImage().split(","));
                    for (String url : saved.split(",")) if (!url.isEmpty() && requested.contains(url)) urls.add(url);
                }
            }
            input.setImage(String.join(",", urls));
        }
        else input.setImage(prodOrderlistMapper.selectByOrderNo(no).getImage());
        input.setModifyBy(SecurityUtils.getUsername()); input.setModifyTime(new Date());
        if (prodOrderlistMapper.update(input) != 1) throw new IllegalStateException("工单修改失败");
        orderImages.deleteByOrderNo(no); writeLinks(no, ids);
        for (Long id : ids) if (!old.contains(id)) orderOutbox.enqueueConfirm(id);
        for (Long id : old) if (!ids.contains(id)) orderOutbox.enqueueDeleteIfUnused(id);
        return 1;
    }

    // 历史实现保留：2026-10-14 改为业务事务与 outbox 驱动。
    //     @Override
    //     public int delete(String productionOrderNo) {
    //         if (!StringUtils.hasText(productionOrderNo)) {
    //             return 0;
    //         }
    //         return prodOrderlistMapper.delete(productionOrderNo);
    //     }
    /** 新增：业务删除提交后才异步清理无引用图片。 */
    @Override @org.springframework.transaction.annotation.Transactional
    public int delete(String no) {
        if (orderImages.lockOrder(no) == null) throw new IllegalArgumentException("工单不存在");
        List<Long> ids = orderImages.selectImageIds(no);
        if (prodOrderlistMapper.delete(no) != 1) throw new IllegalStateException("工单删除失败");
        orderImages.deleteByOrderNo(no);
        for (Long id : ids) orderOutbox.enqueueDeleteIfUnused(id);
        return 1;
    }

    // 历史实现保留：2026-10-14 改为业务事务与 outbox 驱动。
    //     @Override
    //     public int deleteBatch(List<String> orderNoList) {
    //         if (orderNoList == null || orderNoList.isEmpty()) {
    //             return 0;
    //         }
    //         return prodOrderlistMapper.deleteBatch(orderNoList);
    //     }
    /** 新增：批量事务统一覆盖所有业务行、关联与 outbox，失败整批回滚。 */
    @Override @org.springframework.transaction.annotation.Transactional
    public int deleteBatch(List<String> inputs) {
        if (inputs == null || inputs.isEmpty()) return 0;
        int total = 0;
        for (String item : new java.util.LinkedHashSet<>(inputs)) total += delete(item);
        return total;
    }
    /** 新增：服务端限制数量、去重、拒绝空 ID，前端类型校验不能代替此检查。 */
    private List<Long> normalized(List<Long> ids) {
        if (ids == null) return java.util.Collections.emptyList();
        if (ids.size() > 15 || ids.stream().anyMatch(id -> id == null || id <= 0))
            throw new IllegalArgumentException("图片 ID 无效或超过 15 张");
        return new java.util.ArrayList<>(new java.util.LinkedHashSet<>(ids));
    }
    private void requireOrder(ProdOrderlist input) {
        if (input == null || !StringUtils.hasText(input.getProductionOrderNo())) throw new IllegalArgumentException("工单号不能为空");
    }
    private void reserve(String no, List<Long> ids, List<Long> old) {
        for (Long id : ids) if (!old.contains(id)) images.reservePending(id, SecurityUtils.getUserId(), "order:" + no);
    }
    private void writeLinks(String no, List<Long> ids) {
        for (int i = 0; i < ids.size(); i++) if (orderImages.insert(no, ids.get(i), i) != 1)
            throw new IllegalStateException("图片关联写入失败");
    }
}
