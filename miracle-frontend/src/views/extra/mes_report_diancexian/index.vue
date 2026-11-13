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
      <el-form-item label="条码" prop="barcode">
        <el-input
          v-model="queryParams.barcode"
          placeholder="请输入条码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备编号" prop="equipmentNo">
        <el-input
          v-model="queryParams.equipmentNo"
          placeholder="请输入设备编号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="批次号" prop="batchNumber">
        <el-input
          v-model="queryParams.batchNumber"
          placeholder="请输入批次号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="结果" prop="overaJudgment" style = "width: 200px;">
        <el-select
          v-model="queryParams.overaJudgment"
          placeholder="请选择结果"
          clearable
          @change="handleQuery"
        >
          <el-option label="通过" value="OK" />
          <el-option label="失败" value="NG" />
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

    <el-table v-loading="loading" :data="dianCheXianList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" min-width="40" align="center" />
      <el-table-column label="测试时间" align="center" prop="testDateTime" min-width="160" show-overflow-tooltip/>
      <el-table-column label="条码" align="center" prop="barcode" min-width="140" show-overflow-tooltip/>
      <el-table-column label="设备编号" align="center" prop="equipmentNo" min-width="80" show-overflow-tooltip/>
      <el-table-column label="批次号" align="center" prop="batchNumber" min-width="80" show-overflow-tooltip/>
      <el-table-column label="绝缘1" align="center" prop="insulation1" min-width="90" show-overflow-tooltip/>
      <!-- <el-table-column label="绝缘2" align="center" prop="insulation2" min-width="90" show-overflow-tooltip/> -->
      <el-table-column label="绝缘3" align="center" prop="insulation3" min-width="90" show-overflow-tooltip/>
      <!-- <el-table-column label="绝缘4" align="center" prop="insulation4" min-width="90" show-overflow-tooltip/> -->
      <el-table-column label="绝缘5" align="center" prop="insulation5" min-width="90" show-overflow-tooltip/>
      <el-table-column label="绝缘6" align="center" prop="insulation6" min-width="90" show-overflow-tooltip/>
      <el-table-column label="耐压1" align="center" prop="voltageResistance1" min-width="90" show-overflow-tooltip/>
      <el-table-column label="耐压2" align="center" prop="voltageResistance2" min-width="90" show-overflow-tooltip/>
      <el-table-column label="耐压3" align="center" prop="voltageResistance3" min-width="90" show-overflow-tooltip/>
      <el-table-column label="耐压5" align="center" prop="voltageResistance5" min-width="90" show-overflow-tooltip/>
      <el-table-column label="极地绝缘" align="center" prop="polarRegionInsulatio" min-width="90" show-overflow-tooltip/>
      <el-table-column label="绝缘1合格" align="center" prop="insulation1Qualified" min-width="60" show-overflow-tooltip/>
      <!-- <el-table-column label="绝缘2合格" align="center" prop="insulation2Qualified" min-width="60" show-overflow-tooltip/> -->
      <el-table-column label="绝缘3合格" align="center" prop="insulation3Qualified" min-width="60" show-overflow-tooltip/>
      <!-- <el-table-column label="绝缘4合格" align="center" prop="insulation4Qualified" min-width="60" show-overflow-tooltip/> -->
      <el-table-column label="绝缘5合格" align="center" prop="insulatio5Qualified" min-width="60" show-overflow-tooltip/>
      <el-table-column label="绝缘6合格" align="center" prop="insulatio6Qualified" min-width="60" show-overflow-tooltip/>
      <el-table-column label="耐压1合格" align="center" prop="voltageResistance1Qualified" min-width="60" show-overflow-tooltip/>
      <!-- <el-table-column label="耐压2合格" align="center" prop="voltageResistance2Qualified" min-width="60" show-overflow-tooltip/> -->
      <el-table-column label="耐压3合格" align="center" prop="voltageResistance3Qualified" min-width="60" show-overflow-tooltip/>
      <el-table-column label="耐压5合格" align="center" prop="voltageResistance5Qualified" min-width="60" show-overflow-tooltip/>
      <el-table-column label="极地绝缘合格" align="center" prop="polarRegionInsulatioQualified" min-width="60" show-overflow-tooltip/>
      <!-- <el-table-column label="导通1合格" align="center" prop="connect1Qualified" min-width="60" show-overflow-tooltip/> -->
      <!-- <el-table-column label="导通2合格" align="center" prop="connect2Qualified" min-width="60" show-overflow-tooltip/> -->
      <!-- <el-table-column label="导通3合格" align="center" prop="connect3Qualified" min-width="60" show-overflow-tooltip/> -->
      <!-- <el-table-column label="导通4合格" align="center" prop="connect4Qualified" min-width="60" show-overflow-tooltip/> -->
      <el-table-column label="主电容容量" align="center" prop="interpolarCapability" min-width="60" show-overflow-tooltip/>
      <el-table-column label="主电容损耗" align="center" prop="interpolarLoss" min-width="60" show-overflow-tooltip/>
      <el-table-column label="接地电容容量" align="center" prop="polarRegionCapability" min-width="60" show-overflow-tooltip/>
      <el-table-column label="极地电容损耗" align="center" prop="polarRegionLoss" min-width="60" show-overflow-tooltip/>
      <el-table-column label="主电容容损合格" align="center" prop="interpolarQualified" min-width="60" show-overflow-tooltip/>
      <el-table-column label="接地电容容损合格" align="center" prop="polarRegionQualified" min-width="60" show-overflow-tooltip/>
      <el-table-column label="总体判定" align="center" prop="overaJudgment" min-width="60" show-overflow-tooltip/>
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

<script setup name="MesReportDianCeeXian">
import { ref, reactive, toRefs } from "vue";
import { getCurrentInstance } from "vue";
import { listMes_report_diancexian } from "@/api/extra/mes_report_diancexian";

const { proxy } = getCurrentInstance();

const dianCheXianList = ref([]);
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
    barcode: null,
    equipmentNo: null,
    batchNumber: null,
    testStartTime: null,
    testEndTime: null,
    overaJudgment:null
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
  listMes_report_diancexian(queryParams.value).then(response => {
    dianCheXianList.value = response.rows;
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
    '/extra/mes/report/mes_report_diancexian/export',
    { ...queryParams.value },
    `mes_report_dianchexian_${new Date().getTime()}.xlsx`
  );
}

getList();
</script>