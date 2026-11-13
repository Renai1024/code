<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="测试时间" prop="startTime">
        <el-date-picker clearable
          v-model="queryParams.startTime"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择测试时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="测试时间" prop="testTimeRange">
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
      <el-form-item label="设备编码" prop="machineId">
        <el-input
          v-model="queryParams.machineId"
          placeholder="请输入设备编码"
          clearable
          @keyup.enter="handleQuery"
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
      <el-form-item label="作业员" prop="operator">
        <el-input
          v-model="queryParams.operator"
          placeholder="请输入作业员"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="结果" prop="result">
        <el-input
          v-model="queryParams.result"
          placeholder="请输入结果"
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

    <el-table v-loading="loading" :data="mes_report_zcDaoTongGaoYaCeShiList" @selection-change="handleSelectionChange">
      <el-table-column label="站号" align="center" prop="stationId" min-width="80" show-overflow-tooltip/>
      <el-table-column label="设备编码" align="center" prop="machineId" min-width="80" show-overflow-tooltip/>
      <el-table-column label="条码" align="center" prop="sn" min-width="120" show-overflow-tooltip/>
      <el-table-column label="条码2" align="center" prop="sn2" min-width="120" show-overflow-tooltip/>
      <el-table-column label="生产型号" align="center" prop="pn" min-width="100" show-overflow-tooltip/>
      <el-table-column label="生产订单号" align="center" prop="orderId" min-width="100" show-overflow-tooltip/>
      <el-table-column label="批次号" align="center" prop="batchId" min-width="100" show-overflow-tooltip/>
      <el-table-column label="作业员" align="center" prop="operator" min-width="80" show-overflow-tooltip/>
      <el-table-column label="结果" align="center" prop="result" min-width="80" show-overflow-tooltip/>
      <el-table-column type="selection" min-width="40" align="center" />
      <el-table-column label="开始时间" align="center" prop="startTime" min-width="120" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ parseTime(scope.row.startTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" align="center" prop="endTime" min-width="120" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ parseTime(scope.row.endTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="测试数据" align="center" prop="dataResult" min-width="200" show-overflow-tooltip/>
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

<script setup name="Mes_report_zcDaoTongGaoYaCeShi">
import { listMes_report_zcDaoTongGaoYaCeShi} from "@/api/extra/mes_report_zcDaoTongGaoYaCeShi";
import {ref} from "vue";

const { proxy } = getCurrentInstance();

const mes_report_zcDaoTongGaoYaCeShiList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const testTimeRange = ref([]);

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    machineId: null,
    sn:null,
    operator:null,
    startTime:null,
    testStartTime: null,
    testEndTime: null,
    result:null
  },
  rules: {
  }
});

const { queryParams, form, rules } = toRefs(data);

// 转序日期范围变化处理
function handleDateRangeChange(range) {
  if (range && range.length === 2) {
    queryParams.value.testStartTime = range[0];
    queryParams.value.testEndTime = range[1];
  } else {
    queryParams.value.testStartTime = null;
    queryParams.value.testEndTime = null;
  }
}

/** 查询中测导通高压测试数据列表 */
function getList() {
  loading.value = true;
  listMes_report_zcDaoTongGaoYaCeShi(queryParams.value).then(response => {
    mes_report_zcDaoTongGaoYaCeShiList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}


// 表单重置
function reset() {
  form.value = {
    id: null,
    stationId: null,
    machineId: null,
    sn: null,
    sn2: null,
    pn: null,
    orderId: null,
    batchId: null,
    operator: null,
    result: null,
    startTime: null,
    endTime: null,
    dataResult: null,
    createdTime: null
  };
  proxy.resetForm("mes_report_zcDaoTongGaoYaCeShiRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}


/** 导出按钮操作 */
function handleExport() {
  proxy.download('extra/mes/report/mes_report_zcDaoTongGaoYaCeShi/export', {
    ...queryParams.value
  }, `mes_report_zcDaoTongGaoYaCeShi_${new Date().getTime()}.xlsx`)
}

getList();
</script>
