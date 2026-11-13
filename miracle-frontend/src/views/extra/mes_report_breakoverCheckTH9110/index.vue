<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="测试时间范围" prop="checkTimeRange">
        <el-date-picker
          v-model="checkTimeRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          @change="handleCheckTimeRangeChange"
          clearable
          :unlink-panels="true"
        />
      </el-form-item>
      <el-form-item label="条码" prop="barCode">
        <el-input
          v-model="queryParams.barCode"
          placeholder="请输入条码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="产品料号" prop="materialCode">
        <el-input
          v-model="queryParams.materialCode"
          placeholder="请输入产品料号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="生产订单" prop="productionOrder">
        <el-input
          v-model="queryParams.productionOrder"
          placeholder="请输入生产订单"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="测试项目" prop="testProject">
        <el-input
          v-model="queryParams.testProject"
          placeholder="请输入测试项目"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="总体结果" prop="overallResultFilter">
        <el-input
          v-model="queryParams.overallResultFilter"
          placeholder="请输入总体结果"
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

    <el-table v-loading="loading" :data="reportList" max-height="600">
      <el-table-column label="测试时间" align="center" prop="checkTime" min-width="160" show-overflow-tooltip fixed>
        <template #default="scope">
          <span>{{ parseTime(scope.row.checkTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="条码" align="center" prop="barCode" min-width="180" show-overflow-tooltip/>
      <el-table-column label="产品料号" align="center" prop="materialCode" min-width="130" show-overflow-tooltip/>
      <el-table-column label="测试步骤" align="center" prop="testStep" min-width="100" show-overflow-tooltip/>
      <el-table-column label="测试项目" align="center" prop="testProject" min-width="100" show-overflow-tooltip/>
      <el-table-column label="测试电压(kV)" align="center" prop="testVoltage" min-width="100" show-overflow-tooltip/>
      <el-table-column label="测试电流(mA)" align="center" prop="testElectricity" min-width="100" show-overflow-tooltip/>
      <el-table-column label="判定结果" align="center" prop="judgmentResult" min-width="100" show-overflow-tooltip />
      <el-table-column label="总体结果" align="center" prop="overallResult" min-width="100" show-overflow-tooltip fixed="right"/>
      <el-table-column label="操作员" align="center" prop="operator" min-width="90" show-overflow-tooltip/>
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

<script setup name="MesReportBreakoverCheckTH9110">
import { ref, reactive, toRefs } from "vue";
import { getCurrentInstance } from "vue";
import { mes_report_breakovercheckth9110 } from "@/api/extra/mes_report_breakovercheckth9110";

const { proxy } = getCurrentInstance();

const reportList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const total = ref(0);
const checkTimeRange = ref([]);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    barCode: null,
    materialCode: null,
    productionOrder: null,
    testProject: null,
    overallResultFilter: null,
    checkStartTime: null,
    checkEndTime: null
  }
});

const { queryParams } = toRefs(data);

function handleCheckTimeRangeChange(range) {
  if (range && range.length === 2) {
    queryParams.value.checkStartTime = range[0];
    queryParams.value.checkEndTime = range[1];
  } else {
    queryParams.value.checkStartTime = null;
    queryParams.value.checkEndTime = null;
  }
}

function getList() {
  loading.value = true;
  mes_report_breakovercheckth9110(queryParams.value).then(response => {
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
  checkTimeRange.value = [];
  queryParams.value.checkStartTime = null;
  queryParams.value.checkEndTime = null;
  queryParams.value.overallResultFilter = null;
  handleQuery();
}

function handleExport() {
  proxy.download(
    'extra/mes/report/mes_report_breakoverCheckTH9110/export',
    { ...queryParams.value },
    `绝缘耐压测试仪TH9110_${new Date().getTime()}.xlsx`
  );
}
getList();
</script>