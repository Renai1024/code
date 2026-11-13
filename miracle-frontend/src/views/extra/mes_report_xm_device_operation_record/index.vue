<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="设备编号" prop="deviceNo">
        <el-input v-model="queryParams.deviceNo" placeholder="请输入设备编号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="设备类型" prop="deviceType">
        <el-select v-model="queryParams.deviceType" placeholder="请输入设备类型" clearable style="width: 300px;">
        </el-select>
      </el-form-item>
      <el-form-item label="产品型号" prop="productModel">
        <el-input v-model="queryParams.productModel" placeholder="请输入产品型号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="工单" prop="workOrder">
        <el-input v-model="queryParams.workOrder" placeholder="请输入工单" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="工作结果" prop="workResults" style="width:220px;">
        <el-select v-model="queryParams.workResults" placeholder="请选择工作结果" clearable>
          <el-option label="OK" value="OK" />
          <el-option label="NG" value="NG" />
          <el-option label="UNKNOW" value="UNKNOW" />
        </el-select>
      </el-form-item>
      <el-form-item label="工作时间范围" prop="workDateRange">
        <el-date-picker
          v-model="workDateRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          @change="handleWorkDateRangeChange"
          clearable
          :unlink-panels="true"
        />
      </el-form-item>
      <el-form-item label="条码查询" prop="searchBarCode">
        <el-input v-model="queryParams.searchBarCode" placeholder="请输入条码" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="物料批次" prop="searchMaterialBatch">
        <el-input v-model="queryParams.searchMaterialBatch" placeholder="请输入物料批次" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="物料条码" prop="searchMaterialBarCode">
        <el-input v-model="queryParams.searchMaterialBarCode" placeholder="请输入物料条码" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="warning" plain icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row> -->

    <el-table v-loading="loading" :data="recordList" @selection-change="handleSelectionChange" max-height="600">
      <el-table-column type="selection" min-width="40" align="center" fixed />
      <el-table-column label="工作时间" align="center" prop="workDate" min-width="160" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ parseTime(scope.row.workDate, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="设备编号" align="center" prop="deviceNo" min-width="150" show-overflow-tooltip/>
      <el-table-column label="设备类型" align="center" prop="deviceType" min-width="120" show-overflow-tooltip/>
      <el-table-column label="产品型号" align="center" prop="productModel" min-width="120" show-overflow-tooltip/>
      <el-table-column label="工单" align="center" prop="workOrder" min-width="150" show-overflow-tooltip/>
      <el-table-column label="工作结果" align="center" prop="workResults" min-width="100" >
        <template #default="scope">
          <el-tag :type="scope.row.workResults === 'OK' ? 'success' : (scope.row.workResults === 'NG' ? 'danger' : 'warning')" size="small">
            {{ scope.row.workResults }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="缺陷原因" align="center" prop="defectReason" min-width="150" show-overflow-tooltip/>
      <el-table-column label="条码列表" align="center" min-width="100">
        <template #default="scope">
          <el-popover placement="right" width="400" trigger="click">
            <template #reference>
              <el-tag type="primary" size="large">{{ scope.row.barCodeList ? scope.row.barCodeList.length : 0 }}条</el-tag>
            </template>
            <div v-if="scope.row.barCodeList && scope.row.barCodeList.length > 0">
              <el-tag v-for="(item, index) in scope.row.barCodeList" :key="index" size="large" class="mb2">{{ item.barCode }}</el-tag>
            </div>
            <span v-else>-</span>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="物料批次" align="center" min-width="100">
        <template #default="scope">
          <el-popover placement="right" width="400" trigger="click" style="height: auto;">
            <template #reference>
              <el-tag type="info" size="large">{{ scope.row.materialBatchList ? scope.row.materialBatchList.length : 0 }}条</el-tag>
            </template>
            <div v-if="scope.row.materialBatchList && scope.row.materialBatchList.length > 0">
              <el-tag v-for="(item, index) in scope.row.materialBatchList" :key="index" size="large" class="mb2">{{item.materialModel}}：{{ item.materialBatch }}</el-tag>
            </div>
            <span v-else>-</span>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="物料条码" align="center" min-width="100">
        <template #default="scope">
          <el-popover placement="right" width="400" trigger="click">
            <template #reference>
              <el-tag type="warning" size="large">{{ scope.row.materialBarCodeList ? scope.row.materialBarCodeList.length : 0 }}条</el-tag>
            </template>
            <div v-if="scope.row.materialBarCodeList && scope.row.materialBarCodeList.length > 0">
              <el-tag v-for="(item, index) in scope.row.materialBarCodeList" :key="index" size="large" class="mb2">{{ item.materialBarCode }}</el-tag>
            </div>
            <span v-else>-</span>
          </el-popover>
        </template>
      </el-table-column>
      <el-table-column label="数据详情" align="center" min-width="100">
        <template #default="scope">
          <el-popover placement="right" width="500" trigger="click">
            <template #reference>
              <el-tag type="success" size="large">{{ scope.row.dataInfoList ? scope.row.dataInfoList.length : 0 }}项</el-tag>
            </template>
            <el-table :data="scope.row.dataInfoList || []" style="width: 100%" size="small">
              <el-table-column label="参数名称" align="center" prop="dataName" min-width="120" show-overflow-tooltip/>
              <el-table-column label="参数值" align="center" prop="dataValue" min-width="100" show-overflow-tooltip/>
            </el-table>
          </el-popover>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup name="MesReportXMDeviceOperationRecord">
import { ref, reactive, toRefs } from "vue";
import { getCurrentInstance } from "vue";
import { mes_report_xm_device_operation_record } from "@/api/extra/mes_report_xm_device_operation_record";

const { proxy } = getCurrentInstance();

const recordList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const workDateRange = ref([]);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    deviceNo: null,
    deviceType: null,
    productModel: null,
    workOrder: null,
    workResults: null,
    barCode: null,
    searchBarCode: null,
    searchMaterialBatch: null,
    searchMaterialBarCode: null,
    workDateStart: null,
    workDateEnd: null
  }
});

const { queryParams } = toRefs(data);

function handleWorkDateRangeChange(range) {
  if (range && range.length === 2) {
    queryParams.value.workDateStart = range[0];
    queryParams.value.workDateEnd = range[1];
  } else {
    queryParams.value.workDateStart = null;
    queryParams.value.workDateEnd = null;
  }
}

function getList() {
  loading.value = true;
  mes_report_xm_device_operation_record(queryParams.value).then(response => {
    recordList.value = response.rows;
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
  workDateRange.value = [];
  queryParams.value.workDateStart = null;
  queryParams.value.workDateEnd = null;
  handleQuery();
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length !== 1;
  multiple.value = selection.length === 0;
}

function handleExport() {
  proxy.download(
    'extra/mes/report/mes_report_xm_device_operation_record/export',
    { ...queryParams.value },
    `XM设备操作记录报表_${new Date().getTime()}.xlsx`
  );
}

getList();
</script>

<style scoped>
.mb2 {
  margin-bottom: 4px;
  display: block;
  padding-top: 10px;
  color: black;
}
</style>