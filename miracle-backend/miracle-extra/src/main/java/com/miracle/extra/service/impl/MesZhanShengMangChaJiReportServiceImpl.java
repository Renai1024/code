package com.miracle.extra.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.miracle.extra.domain.MesZhanShengMangChaJiReport;
import com.miracle.extra.mapper.MesZhanShengMangChaJiReportMapper;
import com.miracle.extra.service.IMesZhanShengMangChaJiReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesZhanShengMangChaJiReportServiceImpl implements IMesZhanShengMangChaJiReportService {

    @Autowired
    private MesZhanShengMangChaJiReportMapper mesZhanShengMangChaJiReportMapper;

    @Override
    public List<MesZhanShengMangChaJiReport> selectMesZhanShengMangChaJiReportList(MesZhanShengMangChaJiReport report) {
        List<MesZhanShengMangChaJiReport> list = mesZhanShengMangChaJiReportMapper.selectMesZhanShengMangChaJiReportList(report);
        
        // 解析JSON数据并格式化
        for (MesZhanShengMangChaJiReport item : list) {
            if (item.getDatas() != null && !item.getDatas().isEmpty()) {
                try {
                    List<MesZhanShengMangChaJiReport.ColorData> dataList = JSON.parseObject(
                        item.getDatas(), 
                        new TypeReference<List<MesZhanShengMangChaJiReport.ColorData>>() {}
                    );
                    item.setDataList(dataList);
                    
                    // 格式化用于导出
                    StringBuilder formatted = new StringBuilder();
                    for (int i = 0; i < dataList.size(); i++) {
                        MesZhanShengMangChaJiReport.ColorData data = dataList.get(i);
                        formatted.append(data.getColor())
                                .append(": ")
                                .append(data.getColorPrint());
                        if (i < dataList.size() - 1) {
                            formatted.append("; ");
                        }
                    }
                    item.setFormattedDatas(formatted.toString());
                } catch (Exception e) {
                    item.setFormattedDatas(item.getDatas());
                }
            }
        }
        
        return list;
    }
}