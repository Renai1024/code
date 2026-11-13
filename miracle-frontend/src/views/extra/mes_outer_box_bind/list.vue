<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="绑定时间范围" prop="bindTimeRange">
        <el-date-picker
          v-model="bindTimeRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          @change="handleBindTimeRangeChange"
          clearable
          :unlink-panels="true"
        />
      </el-form-item>
      <el-form-item label="外箱编码" prop="outerBoxBarcode">
        <el-input
          v-model="queryParams.outerBoxBarcode"
          placeholder="请输入外箱编码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="产品编码" prop="productBarcode">
        <el-input
          v-model="queryParams.productBarcode"
          placeholder="请输入产品编码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="绑定人" prop="bindBy">
        <el-input
          v-model="queryParams.bindBy"
          placeholder="请输入绑定人"
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

    <el-table v-loading="loading" :data="outerBoxBindList" @selection-change="handleSelectionChange" max-height="600">
      <el-table-column type="selection" min-width="40" align="center" fixed />
      <el-table-column label="外箱编码" align="center" prop="outerBoxBarcode" min-width="180" show-overflow-tooltip fixed />
      <el-table-column label="产品编码" align="center" prop="productBarcode" min-width="180" show-overflow-tooltip/>
      <el-table-column label="绑定顺序" align="center" prop="bindSn" min-width="100" show-overflow-tooltip/>
      <el-table-column label="绑定时间" align="center" min-width="160" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ parseTime(scope.row.bindTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="绑定人" align="center" prop="bindBy" min-width="100" show-overflow-tooltip />
      <el-table-column label="操作" align="center" min-width="100" fixed="right" v-hasPermi="['extra:outer_box_bind:delete']">
        <template #default="scope">
          <el-button
            size="small"
            type="danger"
            @click="handleDelete(scope.row.outerBoxBarcode)"
            :confirm="true"
            confirm-text="确定要删除该外箱编码的所有绑定记录吗？"
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

<script setup name="MesOuterBoxBindList">
import { ref, reactive, toRefs } from "vue";
import { getCurrentInstance } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { listOuterBoxBind, delOuterBoxBind } from "@/api/extra/mes_outer_box_bind";

const { proxy } = getCurrentInstance();

const outerBoxBindList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const bindTimeRange = ref([]);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    outerBoxBarcode: null,
    productBarcode: null,
    bindBy: null,
    bindStartTime: null,
    bindEndTime: null
  }
});

const { queryParams } = toRefs(data);

function handleBindTimeRangeChange(range) {
  if (range && range.length === 2) {
    queryParams.value.bindStartTime = range[0];
    queryParams.value.bindEndTime = range[1];
  } else {
    queryParams.value.bindStartTime = null;
    queryParams.value.bindEndTime = null;
  }
}

function getList() {
  loading.value = true;
  listOuterBoxBind(queryParams.value).then(response => {
    outerBoxBindList.value = response.rows;
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
  bindTimeRange.value = [];
  queryParams.value.bindStartTime = null;
  queryParams.value.bindEndTime = null;
  handleQuery();
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.uuid);
  single.value = selection.length !== 1;
  multiple.value = selection.length === 0;
}

function handleExport() {
  proxy.download(
    'extra/mes/outerBoxBind/export',
    { ...queryParams.value },
    `外箱绑定记录_${new Date().getTime()}.xlsx`
  );
}

function handleDelete(outerBoxBarcode) {
  ElMessageBox.confirm(
    '确定要删除该外箱编码的所有绑定记录吗？',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    delOuterBoxBind(outerBoxBarcode).then(response => {
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
    // 取消删除
  });
}


getList();
</script>

<style scoped>
/* 可根据需要添加样式 */
</style>