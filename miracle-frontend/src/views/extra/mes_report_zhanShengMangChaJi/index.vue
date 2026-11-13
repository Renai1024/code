<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="操作时间范围" prop="createTimeRange">
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
      <el-form-item label="条码" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入条码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备编码" prop="macheineCode">
        <el-input
          v-model="queryParams.macheineCode"
          placeholder="请输入设备编码"
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

    <el-table v-loading="loading" :data="zhanShengMangChaJiList" @selection-change="handleSelectionChange" max-height="600">
      <el-table-column label="条码" align="center" prop="code" min-width="150" show-overflow-tooltip/>
      <el-table-column label="操作时间" align="center" prop="uploadTime" min-width="160" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ parseTime(scope.row.uploadTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="设备编码" align="center" prop="macheineCode" min-width="120" show-overflow-tooltip/>
      <el-table-column label="颜色数据" align="center" min-width="100">
        <template #default="scope">
          <template v-if="scope.row.dataList && scope.row.dataList.length > 0">
            <el-popover
              placement="right"
              width="400"
              trigger="click"
            >
              <template #reference>
                <el-tag type="primary" size="small">{{ scope.row.dataList.length }}条</el-tag>
              </template>
              <el-table :data="scope.row.dataList" style="width: 100%" size="small">
                <el-table-column label="序号" align="center" width="60">
                  <template #default="innerScope">
                    <span>{{ innerScope.$index + 1 }}</span>
                  </template>
                </el-table-column>
                <el-table-column label="颜色" align="center" prop="color" min-width="100" show-overflow-tooltip/>
                <el-table-column label="颜色标记" align="center" prop="colorPrint" min-width="100" show-overflow-tooltip/>
              </el-table>
            </el-popover>
          </template>
          <span v-else>-</span>
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

<script setup name="MesReportZhanShengMangChaJi">
import { ref, reactive, toRefs } from "vue";
import { getCurrentInstance } from "vue";
import { mes_report_zhanShengMangChaJi } from "@/api/extra/mes_report_zhanShengMangChaJi";

const { proxy } = getCurrentInstance();

const zhanShengMangChaJiList = ref([]);
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
    pageSize: 10,
    code: null,
    macheineCode: null,
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
  mes_report_zhanShengMangChaJi(queryParams.value).then(response => {
    zhanShengMangChaJiList.value = response.rows;
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
    'extra/mes/report/mes_report_zhanShengMangChaJi/export',
    { ...queryParams.value },
    `展昇盲插机报表_${new Date().getTime()}.xlsx`
  );
}


getList();
</script>

<style scoped>
</style>