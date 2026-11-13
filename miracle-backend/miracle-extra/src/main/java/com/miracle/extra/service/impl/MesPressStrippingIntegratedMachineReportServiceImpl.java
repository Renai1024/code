package com.miracle.extra.service.impl;

import com.miracle.extra.domain.MesPressStrippingIntegratedMachineReport;
import com.miracle.extra.mapper.MesPressStrippingIntegratedMachineReportMapper;
import com.miracle.extra.service.IMesPressStrippingIntegratedMachineReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MesPressStrippingIntegratedMachineReportServiceImpl implements IMesPressStrippingIntegratedMachineReportService {

    @Autowired
    private MesPressStrippingIntegratedMachineReportMapper mesPressStrippingIntegratedMachineReportMapper;

    @Override
    public List<MesPressStrippingIntegratedMachineReport> selectMesPressStrippingIntegratedMachineReportList(MesPressStrippingIntegratedMachineReport report) {
        return mesPressStrippingIntegratedMachineReportMapper.selectMesPressStrippingIntegratedMachineReportList(report);
    }
}
