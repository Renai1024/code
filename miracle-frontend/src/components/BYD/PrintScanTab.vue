<!-- 弃用 -->
<template>
  <div>
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="物料号" prop="materialCode">
        <el-input
            v-model="queryParams.materialCode"
            placeholder="请输入物料号"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="条码" prop="barCodeStr">
        <el-input
            v-model="queryParams.barCodeStr"
            placeholder="请输入条码"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
<!--      <el-form-item label="扫码时间" prop="scanDate">-->
<!--        <el-date-picker clearable-->
<!--                        v-model="queryParams.scanDate"-->
<!--                        type="date"-->
<!--                        value-format="YYYY-MM-DD"-->
<!--                        placeholder="请选择扫码时间">-->
<!--        </el-date-picker>-->
<!--      </el-form-item>-->
      
      <el-form-item label="标签日期" prop="labelTimeRange">
        <el-date-picker
            v-model="labelTimeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateRangeChangeLabel"
            clearable
            :unlink-panels="true"
        />
      </el-form-item>
      <el-form-item label="打印时间点" prop="printTimeRange">
        <el-date-picker
            v-model="printTimeRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            @change="handleDateRangeChangePrint"
            clearable
            :unlink-panels="true"
        />
      </el-form-item>
      <el-form-item label="扫码时间" prop="lockTimeRange">
        <el-date-picker
            v-model="scanTimeRange"
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
<!--      <el-form-item label="扫码所在电脑名称" prop="scanMachine">-->
<!--        <el-input-->
<!--            v-model="queryParams.scanMachine"-->
<!--            placeholder="请输入扫码所在电脑名称"-->
<!--            clearable-->
<!--            @keyup.enter="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="扫码所在电脑" prop="scanIPAddress">-->
<!--        <el-input-->
<!--            v-model="queryParams.scanIPAddress"-->
<!--            placeholder="请输入扫码所在电脑"-->
<!--            clearable-->
<!--            @keyup.enter="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="是否重码" prop="duplicateFlag">-->
<!--        <el-input-->
<!--            v-model="queryParams.duplicateFlag"-->
<!--            placeholder="请输入是否重码"-->
<!--            clearable-->
<!--            @keyup.enter="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
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

    <el-table v-loading="loading" :data="bydlabelprintScanList" @selection-change="handleSelectionChange" height="500px">
      <el-table-column type="selection" width="30" align="center" />
      <el-table-column label="物料号" align="center" prop="materialCode" min-width="100" show-overflow-tooltip/>
      <el-table-column label="版本号" align="center" prop="materialVersion" min-width="60" show-overflow-tooltip/>
      <el-table-column label="客户编号" align="center" prop="customerCode" min-width="100" show-overflow-tooltip/>
      <el-table-column label="电镀标识" align="center" prop="ddfactoryFlag" min-width="80" show-overflow-tooltip/>
      <el-table-column label="条码" align="center" prop="barCodeStr" min-width="180" show-overflow-tooltip/>
      <el-table-column label="标签流水号" align="center" prop="labelNumber" min-width="120" show-overflow-tooltip/>
      <el-table-column label="标签日期" align="center" prop="manufactureDate" min-width="90" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ parseTime(scope.row.manufactureDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="打印时间点" align="center" prop="LabelPrintDateTime" min-width="120" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ parseTime(scope.row.labelPrintDateTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="扫码时间" align="center" prop="scanDate" min-width="120" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ parseTime(scope.row.scanDate, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="扫码所在电脑名称" align="center" prop="scanMachine" min-width="120" show-overflow-tooltip/>
      <el-table-column label="扫码所在电脑" align="center" prop="scanIPAddress" min-width="120" show-overflow-tooltip/>
      <el-table-column label="是否重码" align="center" prop="duplicateFlag" min-width="60" show-overflow-tooltip/>
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

<script setup name="PrintScanTab">
import { listBydlabelprintscan } from "@/api/extra/mes_report_bydlabelprint.js";
import { ref, reactive, toRefs, getCurrentInstance } from 'vue'
import {ElMessage} from "element-plus";

const { proxy } = getCurrentInstance()

const bydlabelprintScanList = ref([])
const loading = ref(false)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const printTimeRange = ref([]);
const labelTimeRange = ref([]);
const scanTimeRange = ref([]);


const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    materialCode: null,
    barCodeStr: null,
    scanDate: null,
    scanMachine: null,
    scanIPAddress: null,
    duplicateFlag: null
  }
})

const { queryParams, form } = toRefs(data)

// 日期范围变化处理
function handleDateRangeChange(range) {
  if (range && range.length === 2) {
    queryParams.value.startTime = range[0];
    queryParams.value.endTime = range[1];
  } else {
    queryParams.value.startTime = null;
    queryParams.value.endTime = null;
  }
}

// 打印日期范围变化处理
function handleDateRangeChangePrint(range) {
  if (range && range.length === 2) {
    queryParams.value.printStartTime = range[0];
    queryParams.value.printEndTime = range[1];
  } else {
    queryParams.value.printStartTime = null;
    queryParams.value.printEndTime = null;
  }
}

// 标签日期范围变化处理
function handleDateRangeChangeLabel(range) {
  if (range && range.length === 2) {
    queryParams.value.labelStartTime = range[0];
    queryParams.value.labelEndTime = range[1];
  } else {
    queryParams.value.labelStartTime = null;
    queryParams.value.labelEndTime = null;
  }
}

/** 查询bydlabelscan列表 */
function getList() {
  loading.value = true
  listBydlabelprintscan(queryParams.value).then(response => {
    bydlabelprintScanList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

/** 搜索按钮操作 */
function handleQuery() {
  // 校验必填条件
  if (!queryParams.value.materialCode) {
    ElMessage.warning('请输入物料号')
    return
  }

  // if (!queryParams.value.startTime || !queryParams.value.endTime) {
  //   ElMessage.warning('请选择扫码时间范围')
  //   return
  // }
  const hasLabelTime = queryParams.value.labelStartTime && queryParams.value.labelEndTime
  const hasPrintTime = queryParams.value.printStartTime && queryParams.value.printEndTime

  if (!hasLabelTime && !hasPrintTime) {
    ElMessage.warning('请选择标签日期或打印时间点')
    return
  }

  queryParams.value.pageNum = 1
  getList()
}


/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  scanTimeRange.value = []
  labelTimeRange.value = []
  printTimeRange.value = []
  queryParams.value.startTime = null
  queryParams.value.endTime = null
  queryParams.value.labelStartTime = null
  queryParams.value.labelEndTime = null
  queryParams.value.printStartTime = null
  queryParams.value.printEndTime = null
  bydlabelprintScanList.value = []
  total.value = 0
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.materialCode)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

/** 导出按钮操作 */
// function handleExport() {
//   proxy.download('extra/mes/report/bydlabelscan/export', {
//     ...queryParams.value
//   }, `bydlabelscan_${new Date().getTime()}.xlsx`)
// }

/** 导出按钮操作 */
function handleExport() {
  // 导出前同样需要校验
  if (!queryParams.value.materialCode) {
    ElMessage.warning('请输入物料号')
    return
  }

  // if (!queryParams.value.startTime || !queryParams.value.endTime) {
  //   ElMessage.warning('请选择扫码时间范围')
  //   return
  // }
  const hasLabelTime = queryParams.value.labelStartTime && queryParams.value.labelEndTime
  const hasPrintTime = queryParams.value.printStartTime && queryParams.value.printEndTime

  if (!hasLabelTime && !hasPrintTime) {
    ElMessage.warning('请选择标签日期或打印时间点')
    return
  }

  proxy.download('extra/mes/report/bydlabelscan/export', {
    ...queryParams.value
  }, `bydlabelscan_${new Date().getTime()}.xlsx`)
}

//getList()

defineExpose({
  getList
})
</script>