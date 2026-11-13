package com.miracle.extra.domain;

import cn.hutool.core.date.DateTime;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PlmProjectEmployee {
    private Long id;

    @JsonProperty("部门")
    private String department;

    @JsonProperty("姓名")
    private String name;

    @JsonProperty("工号")
    private String empNo;

    @JsonProperty("事业部")
    private String businessUnit;

    @JsonProperty("项目编号")
    private String projectCode;

    @JsonProperty("担任角色")
    private String asRole;

    @JsonProperty("按期完成任务数")
    private int onTimeCount;

    @JsonProperty("超期完成任务数")
    private int overdueCompletedCount;

    @JsonProperty("待完成任务数")
    private int pendingCount;

    @JsonProperty("超期未完成任务数")
    private int overdueUncompletedCount;

    @JsonProperty("任务总数")
    private int totalTasks;

}
