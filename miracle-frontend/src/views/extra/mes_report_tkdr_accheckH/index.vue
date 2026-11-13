<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="测试时间范围" prop="testTimeRange">
        <el-date-picker
          v-model="testTimeRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          @change="handleDateRangeChange"
          clearable
          :unlink-panels="true"
        />
      </el-form-item>
      <el-form-item label="料号" prop="MyField1">
        <el-input
          v-model="queryParams.MyField3"
          placeholder="请输入料号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="条码" prop="StockInBarCode">
        <el-input
          v-model="queryParams.StockInBarCode"
          placeholder="请输入条码"
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

    <el-table v-loading="loading" :data="TKdrList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" min-width="30" align="center" />
      <el-table-column label="原报文" align="center" prop="resultStr" min-width="200" show-overflow-tooltip/>
      <el-table-column label="测试结果" align="center" prop="myField6" min-width="200" show-overflow-tooltip/>
      <el-table-column label="测试时间" align="center" prop="checkStartTime" min-width="80" show-overflow-tooltip/>
      <el-table-column label="条码" align="center" prop="stockInBarCode" min-width="200" show-overflow-tooltip/>
      <el-table-column label="产品料号" align="center" prop="myField1" min-width="100" show-overflow-tooltip/>
      <el-table-column label="机器编号" align="center" prop="myField2" min-width="100" show-overflow-tooltip/>
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

<script setup name="MesReportTKdrACCheckH">
import { ref, reactive, toRefs } from "vue";
import { getCurrentInstance } from "vue";
import { list_TKdr_WKACCheck } from "@/api/extra/mes_report_wkaccheckh";
import {ElMessage} from "element-plus";

const { proxy } = getCurrentInstance();

const TKdrList = ref([]);
const loading = ref(false);
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
    MyField3: null,
    MyField4:null,
    MyField5:null,
    StockInBarCode: null,
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
  list_TKdr_WKACCheck(queryParams.value).then(response => {
    TKdrList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  }).catch(() => {
    loading.value = false;
  });
}

function handleQuery() {
  const hasTime = queryParams.value.testStartTime && queryParams.value.testEndTime

  if (!hasTime) {
    ElMessage.warning('请选择查询时间范围')
    return
  }

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
  const hasTime = queryParams.value.testStartTime && queryParams.value.testEndTime
  
  if (!hasTime) {
    ElMessage.warning('请选择查询时间范围')
    return
  }

  proxy.download(
    'extra/mes/report/wkaccheckh/tkdr/export',
    { ...queryParams.value },
    `泰科电阻_${new Date().getTime()}.xlsx`
  );
}

//getList();

defineExpose({
  getList
})
</script>