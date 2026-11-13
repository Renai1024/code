<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="测试开始时间" prop="timeRange">
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
      <el-form-item label="生产订单号" prop="materialCodeQuery">
        <el-input
          v-model="queryParams.materialCodeQuery"
          placeholder="请输入生产订单号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="产品料号" prop="productCodeQuery">
        <el-input
          v-model="queryParams.productCodeQuery"
          placeholder="请输入产品料号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作人" prop="operatorQuery">
        <el-input
          v-model="queryParams.operatorQuery"
          placeholder="请输入操作人"
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
      <el-table-column label="测试开始时间" align="center" prop="checkStartTime" min-width="160" show-overflow-tooltip fixed>
        <template #default="scope">
          <span>{{ parseTime(scope.row.checkStartTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="测试结束时间" align="center" prop="checkEndTime" min-width="160" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ parseTime(scope.row.checkEndTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="条码" align="center" prop="barCode" min-width="180" show-overflow-tooltip/>
      <el-table-column label="生产订单号" align="center" prop="materialCode" min-width="130" show-overflow-tooltip/>
      <el-table-column label="产品料号" align="center" prop="myField1" min-width="120" show-overflow-tooltip/>
      <el-table-column label="旧料号" align="center" prop="myField2" min-width="120" show-overflow-tooltip/>
      <el-table-column label="产品名称" align="center" prop="myField3" min-width="150" show-overflow-tooltip/>
      <el-table-column label="操作人" align="center" prop="myField4" min-width="100" show-overflow-tooltip/>
      <el-table-column label="测试原始结果" align="center" prop="resultStr" min-width="200" show-overflow-tooltip/>
      <el-table-column label="测试解析结果" align="center" prop="escapeStr" min-width="200" show-overflow-tooltip/>
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

<script setup name="MesReportSheBeiZuData">
import { ref, reactive, toRefs } from "vue";
import { getCurrentInstance } from "vue";
import { listMes_report_shebeizu } from "@/api/extra/mes_report_shebeizu";

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
    productCodeQuery: null,
    operatorQuery: null,
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
  listMes_report_shebeizu(queryParams.value).then(response => {
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
    '/extra/mes/report/mes_report_shebeizu/export',
    { ...queryParams.value },
    `线材导通测试_${new Date().getTime()}.xlsx`
  );
}

getList();
</script>