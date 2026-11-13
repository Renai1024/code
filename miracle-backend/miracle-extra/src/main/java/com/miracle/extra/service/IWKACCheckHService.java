package com.miracle.extra.service;

import com.miracle.extra.domain.FLACCheckH;
import com.miracle.extra.domain.WKACCheckH;

import java.util.List;

/**
 * WKACCheckH报表
 */
public interface IWKACCheckHService {

    /**
     * 查询万肯(有码)耐压测试数据列表
     * @param wkACCheckH
     * @return
     */
    public List<WKACCheckH> selectWKyouma_WKACCheckHList(WKACCheckH wkACCheckH);

    /**
     * 查询安费诺耐压测试数据列表
     * @param wkACCheckH
     * @return
     */
    public List<WKACCheckH> selectAFN_WKACCheckHList(WKACCheckH wkACCheckH);

    /**
     * 查询泰科电阻测试数据列表
     * @param wkACCheckH
     * @return
     */
    public List<WKACCheckH> selectTKdr_WKACCheckHList(WKACCheckH wkACCheckH);
}
