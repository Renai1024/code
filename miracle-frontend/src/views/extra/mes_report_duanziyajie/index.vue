<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="操作时间范围" prop="testTimeRange">
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
      <el-form-item label="产品条码" prop="proUniqueCode">
        <el-input
          v-model="queryParams.proUniqueCode"
          placeholder="请输入产品条码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="线材类型" prop="wireType">
        <el-input
          v-model="queryParams.wireType"
          placeholder="请输入线材类型"
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
      <el-form-item label="测试结果" prop="result">
        <el-select v-model="queryParams.result" placeholder="请选择" clearable>
          <el-option label="OK" value="OK" />
          <el-option label="NG" value="NG" />
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

    <el-table v-loading="loading" :data="duanZiYaJieList" @selection-change="handleSelectionChange" max-height="600">
      <el-table-column type="selection" min-width="40" align="center" fixed />
      <!-- <el-table-column label="序号" align="center" prop="no" min-width="80" show-overflow-tooltip fixed /> -->
      <el-table-column label="流水号" align="center" prop="serialNumber" min-width="120" show-overflow-tooltip />
      <el-table-column label="产品条码" align="center" prop="proUniqueCode" min-width="150" show-overflow-tooltip />
      <el-table-column label="线材类型" align="center" prop="wireType" min-width="100" show-overflow-tooltip />
      <el-table-column label="压接模式" align="center" prop="pressureWeldingMode" min-width="100" show-overflow-tooltip />
      <el-table-column label="铆接高度" align="center" prop="rivetingHeight" min-width="90" show-overflow-tooltip />
      <el-table-column label="预压行程" align="center" prop="preloadingTrip" min-width="90" show-overflow-tooltip />
      <el-table-column label="保压时间" align="center" prop="holdingTime" min-width="90" show-overflow-tooltip />
      <el-table-column label="峰值压接力" align="center" prop="peakCrimpingLoad" min-width="100" show-overflow-tooltip />
      <el-table-column label="CPK值" align="center" prop="cpk" min-width="70" show-overflow-tooltip />
      <el-table-column label="安定性" align="center" prop="security" min-width="70" show-overflow-tooltip />
      <el-table-column label="峰值判定" align="center" prop="peakJudgement" min-width="80" show-overflow-tooltip />
      <el-table-column label="面积判定" align="center" prop="areaJudgement" min-width="80" show-overflow-tooltip />
      <el-table-column label="偏移判定" align="center" prop="offsetJudgement" min-width="80" show-overflow-tooltip />
      <el-table-column label="少芯线判定" align="center" prop="lessCoreWireJudgement" min-width="90" show-overflow-tooltip />
      <el-table-column label="测试结果" align="center" prop="result" min-width="80" show-overflow-tooltip>
        <template #default="scope">
          <el-tag :type="scope.row.result === 'OK' ? 'success' : 'danger'">
            {{ scope.row.result }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作员" align="center" prop="operators" min-width="90" show-overflow-tooltip />
      <el-table-column label="设备编号" align="center" prop="equipmentNo" min-width="120" show-overflow-tooltip />
      <el-table-column label="操作时间" align="center" prop="dateTime" min-width="160" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ parseTime(scope.row.dateTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
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

<script setup name="MesReportDuanZiYaJie">
import { ref, reactive, toRefs } from "vue";
import { getCurrentInstance } from "vue";
import { listMes_report_duanziyajie } from "@/api/extra/mes_report_duanziyajie";

const { proxy } = getCurrentInstance();

const duanZiYaJieList = ref([]);
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
    productNo: null,
    workStation: null,
    deviceName: null,
    result: null,
    testStartTime: null,
    testEndTime: null
  }
});

const { queryParams } = toRefs(data);

// 日期范围变化处理
function handleDateRangeChange(range) {
  if (range && range.length === 2) {
    queryParams.value.testStartTime = range[0];
    queryParams.value.testEndTime = range[1];
  } else {
    queryParams.value.testStartTime = null;
    queryParams.value.testEndTime = null;
  }
}

/** 查询端子压接数据列表 */
function getList() {
  loading.value = true;
  listMes_report_duanziyajie(queryParams.value).then(response => {
    duanZiYaJieList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  }).catch(() => {
    loading.value = false;
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  testTimeRange.value = [];
  queryParams.value.testStartTime = null;
  queryParams.value.testEndTime = null;
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length !== 1;
  multiple.value = selection.length === 0;
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download(
    'extra/mes/report/mes_report_duanziyajie/export',
    { ...queryParams.value },
    `mes_report_duanziyajie_${new Date().getTime()}.xlsx`
  );
}

// 初始化加载
getList();
</script>