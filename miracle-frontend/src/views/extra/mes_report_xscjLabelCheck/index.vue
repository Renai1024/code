<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="扫码时间范围" prop="scanTimeRange">
        <el-date-picker
          v-model="scanTimeRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          @change="handleScanTimeRangeChange"
          clearable
          :unlink-panels="true"
        />
      </el-form-item>
      <el-form-item label="条码" prop="barCodeStr">
        <el-input
          v-model="queryParams.barCodeStr"
          placeholder="请输入条码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属类型" prop="customerFlag" style="width:  300px;">
        <el-select v-model="queryParams.customerFlag" placeholder="请选择" clearable>
          <el-option label="线束充电枪" value="XSCDQ" />
          <el-option label="线束充电座" value="XSCDZ" />
        </el-select>
      </el-form-item>
      <el-form-item label="操作人" prop="operator">
        <el-input
          v-model="queryParams.operator"
          placeholder="请输入操作人"
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
      <el-form-item label="是否重码" prop="duplicateFlag" style="width:  200px;">
        <el-select v-model="queryParams.duplicateFlag" placeholder="请选择" clearable>
          <el-option label="是" :value="true" />
          <el-option label="否" :value="false" />
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

    <el-table v-loading="loading" :data="scanRecordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" min-width="40" align="center" />
      <el-table-column label="条码" align="center" prop="barCodeStr" min-width="150" show-overflow-tooltip/>
      <el-table-column label="扫码日期" align="center" prop="scanDate" min-width="160" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ parseTime(scope.row.scanDate, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="设备名称" align="center" prop="scanMachine" min-width="120" show-overflow-tooltip/>
      <el-table-column label="设备IP" align="center" prop="scanIPAddress" min-width="130" show-overflow-tooltip/>
      <el-table-column label="所属类型" align="center" prop="customerFlag" min-width="100" show-overflow-tooltip/>
      <el-table-column label="操作人" align="center" prop="operator" min-width="100" show-overflow-tooltip/>
      <el-table-column label="产品料号" align="center" prop="materialCode" min-width="120" show-overflow-tooltip/>
      <el-table-column label="是否重码" align="center" prop="duplicateFlag" min-width="90" show-overflow-tooltip>
        <template #default="scope">
          <el-tag :type="scope.row.duplicateFlag ? 'danger' : 'success'">
            {{ scope.row.duplicateFlag ? '是' : '否' }}
          </el-tag>
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

<script setup name="MesReportXSCJLabelCheck">
import { ref, reactive, toRefs } from "vue";
import { getCurrentInstance } from "vue";
import { listMes_report_xscjLabelCheck } from "@/api/extra/mes_report_xscjLabelCheck";

const { proxy } = getCurrentInstance();

const scanRecordList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const scanTimeRange = ref([]);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    barCodeStr: null,
    customerFlag: null,
    operator: null,
    materialCode: null,
    duplicateFlag: null,
    testStartTime: null,
    testEndTime: null
  }
});

const { queryParams } = toRefs(data);

function handleScanTimeRangeChange(range) {
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
  listMes_report_xscjLabelCheck(queryParams.value).then(response => {
    scanRecordList.value = response.rows;
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
  scanTimeRange.value = [];
  queryParams.value.testStartTime = null;
  queryParams.value.testEndTime = null;
  handleQuery();
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.fid);
  single.value = selection.length !== 1;
  multiple.value = selection.length === 0;
}

function handleExport() {
  proxy.download(
    'extra/mes/report/mes_report_xscj/export',
    { ...queryParams.value },
    `线束车间外观检验条码录入记录_${new Date().getTime()}.xlsx`
  );
}

getList();
</script>