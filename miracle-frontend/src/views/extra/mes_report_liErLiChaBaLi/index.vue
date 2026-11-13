<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="创建时间范围" prop="createTimeRange">
        <el-date-picker
          v-model="createTimeRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          @change="handleCreateTimeRangeChange"
          clearable
          :unlink-panels="true"
        />
      </el-form-item>
      <el-form-item label="操作时间范围" prop="operationTimeRange">
        <el-date-picker
          v-model="operationTimeRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          @change="handleOperationTimeRangeChange"
          clearable
          :unlink-panels="true"
        />
      </el-form-item>
      <el-form-item label="产品类型" prop="productType">
        <el-input
          v-model="queryParams.productType"
          placeholder="请输入产品类型"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="工单" prop="workOrder">
        <el-input
          v-model="queryParams.workOrder"
          placeholder="请输入工单"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否合格" prop="qualified">
        <el-select v-model="queryParams.qualified" placeholder="请选择" clearable style="width: 120px;">
          <el-option label="合格" :value="true" />
          <el-option label="不合格" :value="false" />
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

    <el-table v-loading="loading" :data="liErLiChaBaLiList" @selection-change="handleSelectionChange" max-height="600">
      <el-table-column type="selection" min-width="40" align="center" fixed />
      <!--<el-table-column label="创建时间" align="center" prop="createTime" min-width="160" show-overflow-tooltip fixed>
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>-->
      <el-table-column label="操作时间" align="center" prop="operationTime" min-width="160" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ parseTime(scope.row.operationTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="产品类型" align="center" prop="productType" min-width="150" show-overflow-tooltip/>
      <el-table-column label="工单" align="center" prop="workOrder" min-width="150" show-overflow-tooltip/>
      <el-table-column label="产量" align="center" prop="output" min-width="100" show-overflow-tooltip/>
      <el-table-column label="最大拉力值" align="center" prop="tensionMaxValue" min-width="100" show-overflow-tooltip/>
      <el-table-column label="拉力上限" align="center" prop="tensionUpperLimit" min-width="100" show-overflow-tooltip/>
      <el-table-column label="拉力下限" align="center" prop="tensionLowerLimit" min-width="100" show-overflow-tooltip/>
      <el-table-column label="是否合格" align="center" prop="qualified" min-width="100">
        <template #default="scope">
          <el-tag :type="scope.row.qualified === true ? 'success' : 'danger'" size="small">
            {{ scope.row.qualified === true ? '合格' : '不合格' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page-sizes="[20, 50, 100, 200]"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script setup name="MesReportLiErLiChaBaLi">
import { ref, reactive, toRefs } from "vue";
import { getCurrentInstance } from "vue";
import { mes_report_liErLiChaBaLi } from "@/api/extra/mes_report_liErLiChaBaLi";

const { proxy } = getCurrentInstance();

const liErLiChaBaLiList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const createTimeRange = ref([]);
const operationTimeRange = ref([]);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 20,
    productType: null,
    workOrder: null,
    qualified: null,
    createStartTime: null,
    createEndTime: null,
    operationTimeStart: null,
    operationTimeEnd: null
  }
});

const { queryParams } = toRefs(data);

function handleCreateTimeRangeChange(range) {
  if (range && range.length === 2) {
    queryParams.value.createStartTime = range[0];
    queryParams.value.createEndTime = range[1];
  } else {
    queryParams.value.createStartTime = null;
    queryParams.value.createEndTime = null;
  }
}

function handleOperationTimeRangeChange(range) {
  if (range && range.length === 2) {
    queryParams.value.operationTimeStart = range[0];
    queryParams.value.operationTimeEnd = range[1];
  } else {
    queryParams.value.operationTimeStart = null;
    queryParams.value.operationTimeEnd = null;
  }
}

function getList() {
  loading.value = true;
  mes_report_liErLiChaBaLi(queryParams.value).then(response => {
    liErLiChaBaLiList.value = response.rows;
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
  createTimeRange.value = [];
  operationTimeRange.value = [];
  queryParams.value.createStartTime = null;
  queryParams.value.createEndTime = null;
  queryParams.value.operationTimeStart = null;
  queryParams.value.operationTimeEnd = null;
  handleQuery();
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length !== 1;
  multiple.value = selection.length === 0;
}

function handleExport() {
  proxy.download(
    'extra/mes/report/mes_report_liErLiChaBaLi/export',
    { ...queryParams.value },
    `立而利插拔力报表_${new Date().getTime()}.xlsx`
  );
}

getList();
</script>

<style scoped>
</style>