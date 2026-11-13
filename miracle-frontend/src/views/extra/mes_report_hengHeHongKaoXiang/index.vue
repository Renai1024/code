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
      <el-form-item label="启动时间范围" prop="startTimeRange">
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
      </el-form-item>
      <el-form-item label="运行标识" prop="runId">
        <el-input
          v-model="queryParams.runId"
          placeholder="请输入运行标识"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="样品编号" prop="sampleCode">
        <el-input
          v-model="queryParams.sampleCode"
          placeholder="请输入样品编号"
          clearable
          @keyup.enter="handleQuery"
        />
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

    <el-table v-loading="loading" :data="hengHeHongKaoXiangList" @selection-change="handleSelectionChange" max-height="600">
      <el-table-column type="selection" min-width="40" align="center" fixed />
      <el-table-column label="运行标识" align="center" prop="runId" min-width="150" show-overflow-tooltip/>
      <el-table-column label="用户名" align="center" prop="userName" min-width="120" show-overflow-tooltip/>
      <el-table-column label="开锁温度" align="center" prop="unlockTemperature" min-width="100" show-overflow-tooltip/>
      <el-table-column label="启动时间" align="center" prop="startTime" min-width="160" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="停止时间" align="center" prop="stopTime" min-width="160" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ parseTime(scope.row.stopTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="运行时间" align="center" min-width="120" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ calculateRunTime(scope.row.startTime, scope.row.stopTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="实时数据快照" align="center" prop="latestSnapshot" min-width="200" show-overflow-tooltip/>
      <el-table-column label="样品数量" align="center" min-width="100">
        <template #default="scope">
          <el-popover
            v-if="scope.row.items && scope.row.items.length > 0"
            placement="right"
            :width="400"
            trigger="hover"
          >
            <template #reference>
              <el-tag type="primary" size="small">{{ scope.row.items.length }}</el-tag>
            </template>
            <div class="sample-list">
              <div v-for="(item, index) in scope.row.items" :key="index" class="sample-item">
                <div class="sample-item-header">
                  <span class="sample-index">{{ index + 1 }}.</span>
                  <span class="sample-code">{{ item.sampleCode }}</span>
                </div>
                <div class="sample-item-time">添加时间: {{ item.addedAt }}</div>
              </div>
            </div>
          </el-popover>
          <span v-else>-</span>
        </template>
      </el-table-column>
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

<script setup name="MesReportHengHeHongKaoXiang">
import { ref, reactive, toRefs } from "vue";
import { getCurrentInstance } from "vue";
import { mes_report_hengHeHongKaoXiang } from "@/api/extra/mes_report_hengHeHongKaoXiang";

const { proxy } = getCurrentInstance();

const hengHeHongKaoXiangList = ref([]);
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
    runId: null,
    sampleCode: null,
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
  mes_report_hengHeHongKaoXiang(queryParams.value).then(response => {
    hengHeHongKaoXiangList.value = response.rows;
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
    'extra/mes/report/mes_report_hengHeHongKaoXiang/export',
    { ...queryParams.value },
    `横河烘箱报表_${new Date().getTime()}.xlsx`
  );
}

function calculateRunTime(startTime, stopTime) {
  if (!startTime || !stopTime) {
    return '-';
  }
  const start = new Date(startTime);
  const stop = new Date(stopTime);
  const diff = stop - start;
  const hours = Math.floor(diff / 3600000);
  const minutes = Math.floor((diff % 3600000) / 60000);
  const seconds = Math.floor((diff % 60000) / 1000);
  return `${hours}小时 ${minutes}分钟 ${seconds}秒`;
}


getList();
</script>

<style scoped>
.sample-list {
  max-height: 300px;
  overflow-y: auto;
}

.sample-item {
  padding: 8px 0;
}

.sample-item:last-child {
  border-bottom: none;
}

.sample-item-header {
  display: flex;
  align-items: center;
  margin-bottom: 4px;
}

.sample-index {
  font-weight: bold;
  margin-right: 8px;
}

.sample-code {
  font-weight: 500;
}

.sample-item-time {
  font-size: 12px;
  padding-left: 20px;
}
</style>