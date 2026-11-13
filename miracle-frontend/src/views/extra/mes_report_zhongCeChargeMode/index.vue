<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="记录时间范围" prop="createTimeRange">
        <el-date-picker
          v-model="createTimeRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          @change="handleCreateTimeRangeChange"
          clearable
          :unlink-panels="true"
        />
      </el-form-item>
      <!-- <el-form-item label="开始时间范围" prop="startTimeRange">
        <el-date-picker
          v-model="startTimeRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          @change="handleStartTimeRangeChange"
          clearable
          :unlink-panels="true"
        />
      </el-form-item> -->
      <!-- <el-form-item label="工位ID" prop="stationId">
        <el-input
          v-model="queryParams.stationId"
          placeholder="请输入工位ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="机器ID" prop="machineId">
        <el-input
          v-model="queryParams.machineId"
          placeholder="请输入机器ID"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item> -->
      <el-form-item label="产品条码" prop="sn">
        <el-input
          v-model="queryParams.sn"
          placeholder="请输入产品条码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="结果" prop="result" style = "width: 200px;">
        <el-select
          v-model="queryParams.result"
          placeholder="请选择结果"
          clearable
          @change="handleQuery"
        >
          <el-option label="通过" value="PASS" />
          <el-option label="失败" value="FAIL" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="Download"
          @click="handleExport"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="zhongCeChargeModeList" @selection-change="handleSelectionChange" max-height="600">
      <el-table-column type="selection" min-width="40" align="center" fixed />
      <el-table-column label="记录时间" align="center" prop="createTime" min-width="160" show-overflow-tooltip fixed>
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="工站" align="center" prop="stationId" min-width="100" show-overflow-tooltip/>
      <el-table-column label="设备编号" align="center" prop="machineId" min-width="100" show-overflow-tooltip/>
      <el-table-column label="产品条码" align="center" prop="sn" min-width="150" show-overflow-tooltip/>
      <el-table-column label="产品条码2" align="center" prop="sn2" min-width="100" show-overflow-tooltip/>
      <el-table-column label="产品编号" align="center" prop="pn" min-width="80" show-overflow-tooltip/>
      <el-table-column label="订单ID" align="center" prop="orderId" min-width="100" show-overflow-tooltip/>
      <el-table-column label="批次ID" align="center" prop="batchId" min-width="80" show-overflow-tooltip/>
      <el-table-column label="用户" align="center" prop="user" min-width="50" show-overflow-tooltip/>
      <el-table-column label="结果" align="center" prop="result" min-width="100" show-overflow-tooltip>
        <template #default="scope">
          <el-tag :type="scope.row.result === 'PASS' ? 'success' : 'danger'" size="small">{{ scope.row.result === 'PASS' ? '通过' : '失败' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="开始时间" align="center" prop="startTime" min-width="160" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" min-width="160" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <!-- <el-table-column label="运行时间" align="center" min-width="120" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ calculateRunTime(scope.row.startTime, scope.row.endTime) }}</span>
        </template>
      </el-table-column> -->
      <el-table-column label="数据" align="center" prop="data" min-width="200" show-overflow-tooltip/>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script setup name="MesReportZhongCeChargeMode">
import { ref, reactive, toRefs } from "vue";
import { getCurrentInstance } from "vue";
import { mes_report_zhongCeChargeMode } from "@/api/extra/mes_report_zhongCeChargeMode";

const { proxy } = getCurrentInstance();

const zhongCeChargeModeList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const createTimeRange = ref([]);
const startTimeRange = ref([]);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    stationId: null,
    machineId: null,
    sn: null,
    sn2: null,
    pn: null,
    orderId: null,
    batchId: null,
    user: null,
    result: null,
    createStartTime: null,
    createEndTime: null,
    startStartTime: null,
    startEndTime: null
  }
});

const { queryParams } = toRefs(data);

function handleCreateTimeRangeChange(range) {
  if (range && range.length === 2) {
    queryParams.value.createStartTime = range[0];
    queryParams.value.createEndTime = range[1];
  } else {
    queryParams.value.createStartTime = null;
    queryParams.value.createEndTime = null;
  }
}

function handleStartTimeRangeChange(range) {
  if (range && range.length === 2) {
    queryParams.value.startStartTime = range[0];
    queryParams.value.startEndTime = range[1];
  } else {
    queryParams.value.startStartTime = null;
    queryParams.value.startEndTime = null;
  }
}

function getList() {
  loading.value = true;
  mes_report_zhongCeChargeMode(queryParams.value).then(response => {
    zhongCeChargeModeList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  }).catch(() => {
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy.resetForm("queryRef");
  createTimeRange.value = [];
  startTimeRange.value = [];
  queryParams.value.createStartTime = null;
  queryParams.value.createEndTime = null;
  queryParams.value.startStartTime = null;
  queryParams.value.startEndTime = null;
  handleQuery();
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length !== 1;
  multiple.value = selection.length === 0;
}

function handleExport() {
  proxy.download(
    'extra/mes/report/mes_report_zhongCeChargeMode/export',
    { ...queryParams.value },
    `中测充电模式二测试机报表_${new Date().getTime()}.xlsx`
  );
}

function calculateRunTime(startTime, endTime) {
  if (!startTime || !endTime) {
    return '-';
  }
  const start = new Date(startTime);
  const end = new Date(endTime);
  const diff = end - start;
  const hours = Math.floor(diff / 3600000);
  const minutes = Math.floor((diff % 3600000) / 60000);
  const seconds = Math.floor((diff % 60000) / 1000);
  return `${hours}小时 ${minutes}分钟 ${seconds}秒`;
}


getList();
</script>

<style scoped>
/* 可根据需要添加样式 */
</style>