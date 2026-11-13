package com.miracle.extra.service.impl;

import com.miracle.extra.domain.FLACCheckH;
import com.miracle.extra.domain.WKACCheckH;
import com.miracle.extra.mapper.FLACCheckHMapper;
import com.miracle.extra.mapper.WKACCheckHMapper;
import com.miracle.extra.service.IFLACCheckHService;
import com.miracle.extra.service.IWKACCheckHService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * FLACCheckH表服务实现类
 */
@Service
public class WKACCheckHServiceImpl implements IWKACCheckHService {

    @Autowired
    private WKACCheckHMapper wkacCheckHMapper;

    @Override
    public List<WKACCheckH> selectWKyouma_WKACCheckHList(WKACCheckH wkACCheckH) {
        wkACCheckH.setCustomerFlag("WKyouma");
        var list =  wkacCheckHMapper.selectWKACCheckHList(wkACCheckH);
        return list;
    }

    @Override
    public List<WKACCheckH> selectAFN_WKACCheckHList(WKACCheckH wkACCheckH) {
        wkACCheckH.setCustomerFlag("AFN");
        wkACCheckH.setResultStr("PASS");
        var list =  wkacCheckHMapper.selectWKACCheckHList(wkACCheckH);
        return list;
    }

    @Override
    public List<WKACCheckH> selectTKdr_WKACCheckHList(WKACCheckH wkACCheckH) {
        wkACCheckH.setCustomerFlag("TK");
        wkACCheckH.setResultStr("BIN1");
        var list =  wkacCheckHMapper.selectWKACCheckHList(wkACCheckH);
        return list;
    }

}
