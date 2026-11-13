package com.miracle.quartz.task;

import com.miracle.extra.service.impl.PlmDataFetchService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component("PlmProjectPullTask")
public class PlmProjectPullTask {

    @Resource
    private PlmDataFetchService plmDataFetchService;

    public void pullPlmProjectDataDaily() {
        plmDataFetchService.fetchPlmProjectDataDaily();
    }

    public PlmProjectPullTask(){}

    public void pullPlmProjectEmployeeDataDaily() {
        plmDataFetchService.fetchPlmProjectEmployeeDataDaily();
    }
}
