<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="检测日期" prop="jianceriqi">
        <el-date-picker clearable
          v-model="queryParams.jianceriqi"
          type="date"
          value-format="YYYY-MM-DD"
          placeholder="请选择检测日期">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="检测日期" prop="testTimeRange">
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
      <el-form-item label="工位" prop="gongwei">
        <el-input
          v-model="queryParams.gongwei"
          placeholder="请输入工位"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="条码" prop="tiaoma">
        <el-input
          v-model="queryParams.tiaoma"
          placeholder="请输入条码"
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

    <el-table v-loading="loading" :data="mes_report_lwsQiMiXingList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" min-width="40" align="center" />
      <el-table-column label="检测日期" align="center" prop="jianceriqi" min-width="160" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ parseTime(scope.row.jianceriqi, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="工位" align="center" prop="gongwei" min-width="120" show-overflow-tooltip/>
      <el-table-column label="条码" align="center" prop="tiaoma" min-width="160" show-overflow-tooltip/>
      <el-table-column label="充气时间" align="center" prop="chongqishijian" min-width="120" show-overflow-tooltip/>
      <el-table-column label="稳压时间" align="center" prop="wenyashijian" min-width="120" show-overflow-tooltip/>
      <el-table-column label="测试时间" align="center" prop="ceshishijian" min-width="160" show-overflow-tooltip/>
      <el-table-column label="测试压力(pa)" align="center" prop="ceshiyali" min-width="120" show-overflow-tooltip/>
      <el-table-column label="泄露值(pa)" align="center" prop="xielouzhi" min-width="120" show-overflow-tooltip/>
      <el-table-column label="结果" align="center" prop="jieguo" min-width="120" show-overflow-tooltip/>
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

<script setup name="Mes_report_lwsQiMiXing">
import { listMes_report_lwsQiMiXing} from "@/api/extra/mes_report_lwsQiMiXing";
import {ref} from "vue";

const { proxy } = getCurrentInstance();

const mes_report_lwsQiMiXingList = ref([]);
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
    jianceriqi: null,
    testStartTime: null,
    testEndTime: null,
    gongwei: null,
    tiaoma: null,
    chongqishijian: null,
    wenyashijian: null,
    ceshishijian: null,
    ceshiyali: null,
    xielouzhi: null,
    jieguo: null
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

/** 查询立维斯气密性测试数据列表 */
function getList() {
  loading.value = true;
  listMes_report_lwsQiMiXing(queryParams.value).then(response => {
    mes_report_lwsQiMiXingList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}


// 表单重置
function reset() {
  form.value = {
    id: null,
    jianceriqi: null,
    testStartTime: null,
    testEndTime: null,
    gongwei: null,
    tiaoma: null,
    chongqishijian: null,
    wenyashijian: null,
    ceshishijian: null,
    ceshiyali: null,
    xielouzhi: null,
    jieguo: null
  };
  proxy.resetForm("mes_report_lwsQiMiXingRef");
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
  proxy.download('extra/mes/report/mes_report_lwsQiMiXing/export', {
    ...queryParams.value
  }, `mes_report_lwsQiMiXing_${new Date().getTime()}.xlsx`)
}

getList();
</script>
