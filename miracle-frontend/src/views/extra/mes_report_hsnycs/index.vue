<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="测试时间范围" prop="timeRange">
        <el-date-picker
          v-model="timeRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          @change="handleTimeRangeChange"
          clearable
          :unlink-panels="true"
        />
      </el-form-item>
      <el-form-item label="条码" prop="barCodeQuery">
        <el-input
          v-model="queryParams.barCodeQuery"
          placeholder="请输入条码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="产品料号" prop="materialCodeQuery">
        <el-input
          v-model="queryParams.materialCodeQuery"
          placeholder="请输入产品料号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="机器编号" prop="machineNoQuery">
        <el-input
          v-model="queryParams.machineNoQuery"
          placeholder="请输入机器编号"
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

    <el-table v-loading="loading" :data="reportList" max-height="600" stripe>
      <el-table-column label="测试时间" align="center" prop="checkStartTime" min-width="160" show-overflow-tooltip fixed>
        <template #default="scope">
          <span>{{ parseTime(scope.row.checkStartTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="条码" align="center" prop="stockInBarCode" min-width="180" show-overflow-tooltip/>
      <el-table-column label="产品料号" align="center" prop="materialCode" min-width="130" show-overflow-tooltip/>
      <el-table-column label="机器编号" align="center" prop="myField1" min-width="120" show-overflow-tooltip/>
      <el-table-column label="测试数据" align="center" prop="resultStr" min-width="250" show-overflow-tooltip/>
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

<script setup name="MesReportHSnycsData">
import { ref, reactive, toRefs } from "vue";
import { getCurrentInstance } from "vue";
import { listMes_report_hsnycs } from "@/api/extra/mes_report_mupai3";

const { proxy } = getCurrentInstance();

const reportList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const timeRange = ref([]);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    barCodeQuery: null,
    materialCodeQuery: null,
    machineNoQuery: null,
    startTimeStart: null,
    startTimeEnd: null
  }
});

const { queryParams } = toRefs(data);

function handleTimeRangeChange(range) {
  if (range && range.length === 2) {
    queryParams.value.startTimeStart = range[0];
    queryParams.value.startTimeEnd = range[1];
  } else {
    queryParams.value.startTimeStart = null;
    queryParams.value.startTimeEnd = null;
  }
}

function getList() {
  loading.value = true;
  listMes_report_hsnycs(queryParams.value).then(response => {
    reportList.value = response.rows;
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
  timeRange.value = [];
  queryParams.value.startTimeStart = null;
  queryParams.value.startTimeEnd = null;
  handleQuery();
}

function handleExport() {
  proxy.download(
    'extra/mes/report/mes_report_mupai3/hsnycs/export',
    { ...queryParams.value },
    `海思耐压测试_${new Date().getTime()}.xlsx`
  );
}

getList();
</script>