<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="绑定时间范围" prop="createTimeRange">
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
      <el-form-item label="产品料号" prop="productCode">
        <el-input
          v-model="queryParams.productCode"
          placeholder="请输入产品料号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="产品二维码" prop="barcodeValue1">
        <el-input
          v-model="queryParams.barcodeValue1"
          placeholder="请输入产品二维码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="扩展码" prop="barcodeValue2">
        <el-input
          v-model="queryParams.barcodeValue2"
          placeholder="请输入扩展码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备编号" prop="deviceNo">
        <el-input
          v-model="queryParams.deviceNo"
          placeholder="请输入设备编号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="操作人" prop="createBy">
        <el-input
          v-model="queryParams.createBy"
          placeholder="请输入操作人"
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

    <el-table v-loading="loading" :data="bindList" @selection-change="handleSelectionChange" max-height="600">
      <el-table-column type="selection" min-width="40" align="center" fixed />
      <el-table-column label="产品料号" align="center" prop="productCode" min-width="120" show-overflow-tooltip />
      <el-table-column label="产品二维码" align="center" prop="barcodeValue1" min-width="180" show-overflow-tooltip />
      <el-table-column label="扩展码" align="center" prop="barcodeValue2" min-width="180" show-overflow-tooltip />
      <el-table-column label="设备编号" align="center" prop="deviceNo" min-width="120" show-overflow-tooltip />
      <el-table-column label="绑定时间" align="center" min-width="160" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作人" align="center" prop="createBy" min-width="100" show-overflow-tooltip />
      <el-table-column label="操作" align="center" min-width="100" fixed="right" v-hasPermi="['extra:barcode_bind:delete']">
        <template #default="scope">
          <el-button
            size="small"
            type="danger"
            @click="handleDelete(scope.row.id)"
            :confirm="true"
            confirm-text="确定要删除该绑定记录吗？"
          >
            删除
          </el-button>
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

<script setup name="BarcodeBindList">
import { ref, reactive, toRefs } from "vue";
import { getCurrentInstance } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { listBarcodeBind, delBarcodeBind } from "@/api/extra/barcode_bind";

const { proxy } = getCurrentInstance();

const bindList = ref([]);
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
    productCode: null,
    barcodeValue1: null,
    barcodeValue2: null,
    deviceNo: null,
    createBy: null,
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
  listBarcodeBind(queryParams.value).then(response => {
    bindList.value = response.rows;
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
    'extra/barcode/bind/export',
    { ...queryParams.value },
    `双码绑定记录_${new Date().getTime()}.xlsx`
  );
}

function handleDelete(id) {
  ElMessageBox.confirm(
    '确定要删除该绑定记录吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    delBarcodeBind(id).then(response => {
      if (response.code === 200) {
        ElMessage.success('删除成功');
        getList();
      } else {
        ElMessage.error('删除失败：' + response.msg);
      }
    }).catch(() => {
      ElMessage.error('删除失败');
    });
  }).catch(() => {
  });
}

getList();
</script>

<style scoped>
</style>