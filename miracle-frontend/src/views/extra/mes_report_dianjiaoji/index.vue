<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="操作时间范围" prop="testTimeRange">
        <el-date-picker
          v-model="testTimeRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          @change="handleDateRangeChange"
          clearable
          :unlink-panels="true"
        />
      </el-form-item>
      <el-form-item label="条码" prop="tiaoma">
        <el-input
          v-model="queryParams.tiaoma"
          placeholder="请输入条码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备ID" prop="deviceId">
        <el-input
          v-model="queryParams.deviceId"
          placeholder="请输入设备ID"
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

    <el-table v-loading="loading" :data="dianJiaoJiRecordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" min-width="40" align="center" />
      <el-table-column label="操作时间" align="center" prop="workDate" min-width="100" show-overflow-tooltip/>
      <el-table-column label="条码" align="center" prop="tiaoma" min-width="150" show-overflow-tooltip/>
      <el-table-column label="设备ID" align="center" prop="deviceId" min-width="120" show-overflow-tooltip/>
      <el-table-column label="灌胶时间" align="center" prop="guanjiaoshijian" min-width="100" show-overflow-tooltip/>
      <el-table-column label="点胶速度" align="center" prop="dianjiaosudu" min-width="100" show-overflow-tooltip/>
      <el-table-column label="结果" align="center" prop="workResults" min-width="90" show-overflow-tooltip/>
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

<script setup name="MesReportDianJiaoJi">
import { ref, reactive, toRefs } from "vue";
import { getCurrentInstance } from "vue";
import { listMes_report_dinajiaoji } from "@/api/extra/mes_report_dianjiaoji";

const { proxy } = getCurrentInstance();

const dianJiaoJiRecordList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const testTimeRange = ref([]);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    tiaoma: null,
    deviceId: null,
    workResults: null,
    testStartTime: null,
    testEndTime: null
  }
});

const { queryParams } = toRefs(data);

function handleDateRangeChange(range) {
  if (range && range.length === 2) {
    queryParams.value.testStartTime = range[0];
    queryParams.value.testEndTime = range[1];
  } else {
    queryParams.value.testStartTime = null;
    queryParams.value.testEndTime = null;
  }
}

function getList() {
  loading.value = true;
  listMes_report_dinajiaoji(queryParams.value).then(response => {
    dianJiaoJiRecordList.value = response.rows;
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
  testTimeRange.value = [];
  queryParams.value.testStartTime = null;
  queryParams.value.testEndTime = null;
  handleQuery();
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length !== 1;
  multiple.value = selection.length === 0;
}

function handleExport() {
  proxy.download(
    'extra/mes/report/mes_report_dianjiaoji/export',
    { ...queryParams.value },
    `点胶机_${new Date().getTime()}.xlsx`
  );
}

getList();
</script>