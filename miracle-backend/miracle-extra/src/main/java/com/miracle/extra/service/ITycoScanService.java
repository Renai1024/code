package com.miracle.extra.service;

import com.miracle.extra.domain.TycoScan;
import java.util.List;

/** 新增：泰科扫码业务接口，控制器依赖接口，与现有模块分层保持一致。 */
public interface ITycoScanService
{
    boolean used(String code);
    boolean boxUsed(String boxCode);
    List<TycoScan.Batch> list();
    List<String> details(String id);
    TycoScan.Batch replay(TycoScan.Submit request);
    TycoScan.Batch submit(TycoScan.Submit request);
    /** 新增：批量逻辑删除箱头及产品明细，返回本次实际删除的箱数。 */
    int deleteByIds(String[] ids);
}
