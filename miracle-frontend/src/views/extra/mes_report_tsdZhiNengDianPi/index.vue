<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="完成时间范围" prop="finishTimeRange">
        <el-date-picker
          v-model="finishTimeRange"
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
      <el-form-item label="条码" prop="sn">
        <el-input
          v-model="queryParams.sn"
          placeholder="请输入条码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="测试结果" prop="result">
        <el-select v-model="queryParams.result" placeholder="请选择" clearable style = "width: 100px;">
          <el-option label="PASS" value="PASS" />
          <el-option label="OKALL" value="OKALL" />
          <el-option label="FAIL" value="FAIL" />
        </el-select>
      </el-form-item>
      <el-form-item label="设备识别码" prop="mesId">
        <el-input
          v-model="queryParams.mesId"
          placeholder="请输入设备识别码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="螺钉序号" prop="demoData1">
        <el-input
          v-model="queryParams.demoData1"
          placeholder="请输入螺钉序号"
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

    <el-table v-loading="loading" :data="tsdZhiNengDianPiList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" min-width="40" align="center" />
      <el-table-column label="条码" align="center" prop="sn" min-width="140" show-overflow-tooltip/>
      <el-table-column label="完成时间" align="center" prop="createTime" min-width="160" show-overflow-tooltip/>
      <el-table-column label="结果" align="center" prop="result" min-width="100" show-overflow-tooltip>
        <template #default="scope">
          <el-tag :type="scope.row.result === 'PASS' || scope.row.result === 'OKALL' ? 'success' : 'danger'">
            {{ scope.row.result }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="设备识别码" align="center" prop="mesId" min-width="120" show-overflow-tooltip/>
      <el-table-column label="螺钉序号" align="center" prop="demoData1" min-width="100" show-overflow-tooltip/>
      <el-table-column label="扭力" align="center" prop="demoData2" min-width="90" show-overflow-tooltip/>
      <el-table-column label="圈数" align="center" prop="demoData3" min-width="90" show-overflow-tooltip/>
      <el-table-column label="运行时间" align="center" prop="demoData4" min-width="100" show-overflow-tooltip/>
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

<script setup name="MesReportTSDZhiNengDianPi">
import { ref, reactive, toRefs } from "vue";
import { getCurrentInstance } from "vue";
import { listTSDZhiNengDianPi } from "@/api/extra/mes_report_tsdzhinengdianpi";

const { proxy } = getCurrentInstance();

const tsdZhiNengDianPiList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const finishTimeRange = ref([]);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    sn: null,
    result: null,
    mesId: null,
    demoData1: null,
    finishStartTime: null,
    finishEndTime: null
  }
});

const { queryParams } = toRefs(data);

function handleDateRangeChange(range) {
  if (range && range.length === 2) {
    queryParams.value.finishStartTime = range[0];
    queryParams.value.finishEndTime = range[1];
  } else {
    queryParams.value.finishStartTime = null;
    queryParams.value.finishEndTime = null;
  }
}

function getList() {
  loading.value = true;
  listTSDZhiNengDianPi(queryParams.value).then(response => {
    tsdZhiNengDianPiList.value = response.rows;
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
  finishTimeRange.value = [];
  queryParams.value.finishStartTime = null;
  queryParams.value.finishEndTime = null;
  handleQuery();
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length !== 1;
  multiple.value = selection.length === 0;
}

function handleExport() {
  proxy.download(
    'extra/mes/report/mes_report_tsdzhinengdianpi/export',
    { ...queryParams.value },
    `tsd_zhi_neng_dian_pi_${new Date().getTime()}.xlsx`
  );
}

getList();
</script>