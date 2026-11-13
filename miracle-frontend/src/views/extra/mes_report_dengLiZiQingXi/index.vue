<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="记录时间范围" prop="createTimeRange">
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
      <el-form-item label="条码1" prop="sn1">
        <el-input
          v-model="queryParams.sn1"
          placeholder="请输入条码1"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="条码2" prop="sn2">
        <el-input
          v-model="queryParams.sn2"
          placeholder="请输入条码2"
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

    <el-table v-loading="loading" :data="dengLiZiQingXiList" @selection-change="handleSelectionChange" max-height="600">
      <el-table-column type="selection" min-width="40" align="center" fixed />
      <!-- <el-table-column label="创建时间" align="center" prop="createTime" min-width="160" show-overflow-tooltip fixed>
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column> -->
      <el-table-column label="操作时间" align="center" prop="ttime" min-width="160" show-overflow-tooltip fixed>
        <template #default="scope">
          <span>{{ parseTime(scope.row.ttime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="条码1" align="center" prop="sn1" min-width="150" show-overflow-tooltip/>
      <el-table-column label="条码2" align="center" prop="sn2" min-width="150" show-overflow-tooltip/>
      <el-table-column label="R轴速度" align="center" prop="rs" min-width="100" show-overflow-tooltip/>
      <el-table-column label="左Y轴速度" align="center" prop="lys" min-width="120" show-overflow-tooltip/>
      <el-table-column label="右Y轴速度" align="center" prop="rys" min-width="120" show-overflow-tooltip/>
      <el-table-column label="X轴速度" align="center" prop="xs" min-width="100" show-overflow-tooltip/>
      <el-table-column label="Z轴速度" align="center" prop="zs" min-width="100" show-overflow-tooltip/>
      <el-table-column label="高度" align="center" prop="height" min-width="100" show-overflow-tooltip/>
      <el-table-column label="角度" align="center" prop="angle" min-width="100" show-overflow-tooltip/>
      <el-table-column label="档位" align="center" prop="tap" min-width="100" show-overflow-tooltip/>
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

<script setup name="MesReportDengLiZiQingXi">
import { ref, reactive, toRefs } from "vue";
import { getCurrentInstance } from "vue";
import { mes_report_dengLiZiQingXi } from "@/api/extra/mes_report_dengLiZiQingXi";

const { proxy } = getCurrentInstance();

const dengLiZiQingXiList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const createTimeRange = ref([]);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 20,
    sn1: null,
    sn2: null,
    rs: null,
    lys: null,
    rys: null,
    xs: null,
    zs: null,
    height: null,
    angle: null,
    tap: null,
    createStartTime: null,
    createEndTime: null
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

function getList() {
  loading.value = true;
  mes_report_dengLiZiQingXi(queryParams.value).then(response => {
    dengLiZiQingXiList.value = response.rows;
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
  queryParams.value.createStartTime = null;
  queryParams.value.createEndTime = null;
  handleQuery();
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length !== 1;
  multiple.value = selection.length === 0;
}

function handleExport() {
  proxy.download(
    'extra/mes/report/mes_report_dengLiZiQingXi/export',
    { ...queryParams.value },
    `等离子清洗机报表_${new Date().getTime()}.xlsx`
  );
}


getList();
</script>

<style scoped>
/* 可根据需要添加样式 */
</style>