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
<!--      <el-form-item label="版本号" prop="materialVersion">-->
<!--        <el-input-->
<!--            v-model="queryParams.materialVersion"-->
<!--            placeholder="请输入版本号"-->
<!--            clearable-->
<!--            @keyup.enter="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="客户编号" prop="customerCode">-->
<!--        <el-input-->
<!--            v-model="queryParams.customerCode"-->
<!--            placeholder="请输入客户编号"-->
<!--            clearable-->
<!--            @keyup.enter="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
<!--      <el-form-item label="电镀标识" prop="dDFactoryFlag">-->
<!--        <el-input-->
<!--            v-model="queryParams.dDFactoryFlag"-->
<!--            placeholder="请输入电镀标识"-->
<!--            clearable-->
<!--            @keyup.enter="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->
      <el-form-item label="条码" prop="barCodeStr">
        <el-input
            v-model="queryParams.barCodeStr"
            placeholder="请输入条码"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
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
      <el-form-item label="标签号" prop="labelNumber">
        <el-radio-group v-model="searchMode" size="small" style="margin-right: 20px;" @change="handleSearchModeChange">
          <el-radio :label="'none'" :value="'none'">不搜索</el-radio>
          <el-radio :label="'single'" :value="'single'">单个标签</el-radio>
          <el-radio :label="'range'" :value="'range'">范围搜索</el-radio>
        </el-radio-group>
        
        <!-- 单个标签号输入 -->
        <el-input
          v-if="searchMode === 'single'"
          v-model.number="queryParams.labelNumber"
          type="number"
          placeholder="请输入标签号"
          clearable
          style="width: 150px;"
          @keyup.enter="handleQuery"
        >
        </el-input>
        
        <!-- 范围搜索输入 -->
        <div v-else-if="searchMode === 'range'">
          <el-input
            v-model.number="rangeStart"
            type="number"
            placeholder="起始标签号"
            clearable
            style="width: 115px; margin-right: 5px;"
            @keyup.enter="handleQuery"
          >
          </el-input>
          <span>至</span>
          <el-input
            v-model.number="rangeEnd"
            type="number"
            placeholder="结束标签号"
            clearable
            style="width: 115px; margin-left: 5px;"
            @keyup.enter="handleQuery"
          >
          </el-input>
        </div>
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

    <el-table v-loading="loading" :data="bydlabelprintList" @selection-change="handleSelectionChange" height="500px">
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

<script setup name="PrintTab">
import { listBydlabelprint } from "@/api/extra/mes_report_bydlabelprint.js";
import { ref, reactive, toRefs, getCurrentInstance,watch } from 'vue'
import {ElMessage} from "element-plus";

const { proxy } = getCurrentInstance()

const bydlabelprintList = ref([])
const loading = ref(false)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)
const printTimeRange = ref([]);
const labelTimeRange = ref([]);

const searchMode = ref('none')

const rangeStart = ref(null)
const rangeEnd = ref(null)

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    materialCode: null,
    materialVersion: null,
    customerCode: null,
    dDFactoryFlag: null,
    barCodeStr: null,
    labelNumber: null,
    manufactureDate: null,
    labelPrintDateTime: null,
    labelStartTime: null,
    labelEndTime: null,
    printStartTime: null,
    printEndTime: null,
    labelNumberStart: null,
    labelNumberEnd: null
  }
})

const { queryParams, form } = toRefs(data)

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

// 处理标签号范围变化
watch([rangeStart, rangeEnd], () => {
  if (searchMode.value === 'range') {
    // 实时同步到查询参数
    queryParams.value.labelNumberStart = rangeStart.value
    queryParams.value.labelNumberEnd = rangeEnd.value
  }
})

/** 查询bydlabelprint列表 */
function getList() {
  loading.value = true
  listBydlabelprint(queryParams.value).then(response => {
    bydlabelprintList.value = response.rows
    total.value = response.total
    loading.value = false
  })
}

// /** 搜索按钮操作 */
// function handleQuery() {
//   queryParams.value.pageNum = 1
//   getList()
// }

/** 搜索按钮操作 */
function handleQuery() {
  // 校验必填条件
  if (!queryParams.value.materialCode) {
    ElMessage.warning('请输入物料号')
    return
  }

  const hasLabelTime = queryParams.value.labelStartTime && queryParams.value.labelEndTime
  const hasPrintTime = queryParams.value.printStartTime && queryParams.value.printEndTime

  if (!hasLabelTime && !hasPrintTime) {
    ElMessage.warning('请选择标签日期或打印时间点')
    return
  }

  if (searchMode.value === 'single') {
    if (queryParams.value.labelNumber === null || queryParams.value.labelNumber === '') {
      ElMessage.warning('请输入标签号')
      return
    }
    
    // 验证是否为有效整数
    if (!isValidInteger(queryParams.value.labelNumber)) {
      ElMessage.warning('标签号必须是有效的整数')
      return
    }
    
    // 清空范围搜索参数
    queryParams.value.labelNumberStart = null
    queryParams.value.labelNumberEnd = null
    
  } else if (searchMode.value === 'range') {
    if (rangeStart.value === null || rangeEnd.value === null) {
      ElMessage.warning('请输入完整的标签号范围')
      return
    }
    
    // 验证是否为有效整数
    if (!isValidInteger(rangeStart.value) || !isValidInteger(rangeEnd.value)) {
      ElMessage.warning('标签号范围必须是有效的整数')
      return
    }
    
    // 校验范围逻辑
    if (rangeStart.value >= rangeEnd.value) {
      ElMessage.warning('起始标签号必须小于结束标签号')
      return
    }
  }
  queryParams.value.pageNum = 1
  getList()
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  labelTimeRange.value = []
  printTimeRange.value = []
  queryParams.value.labelStartTime = null
  queryParams.value.labelEndTime = null
  queryParams.value.printStartTime = null
  queryParams.value.printEndTime = null
  bydlabelprintList.value = []
  searchMode.value = 'none'
  queryParams.value.labelNumberStart = null
  queryParams.value.labelNumberEnd = null
  queryParams.value.labelNumber = null
  total.value = 0
}

function handleSearchModeChange(newMode) {
  // 清空所有标签号相关字段
  queryParams.value.labelNumber = null
  queryParams.value.labelNumberStart = null
  queryParams.value.labelNumberEnd = null
  
  // 清空临时范围变量
  rangeStart.value = null
  rangeEnd.value = null
  
  // 特殊情况：如果是范围模式，确保两个输入框都清空
  if (newMode !== 'range') {
    // 这里可以添加一些视觉反馈
  }
}

// 验证是否为有效整数
function isValidInteger(value) {
  if (value === null || value === '' || isNaN(value)) {
    return false
  }
  
  const num = Number(value)
  return Number.isInteger(num) // && num >= 0 && num <= 9999999999
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.MaterialCode)
  single.value = selection.length != 1
  multiple.value = !selection.length
}

// /** 导出按钮操作 */
// function handleExport() {
//   proxy.download('extra/mes/report/bydlabelprint/export', {
//     ...queryParams.value
//   }, `bydlabelprint_${new Date().getTime()}.xlsx`)
// }

/** 导出按钮操作 */
function handleExport() {
  // 导出前同样需要校验
  if (!queryParams.value.materialCode) {
    ElMessage.warning('请输入物料号')
    return
  }

  const hasLabelTime = queryParams.value.labelStartTime && queryParams.value.labelEndTime
  const hasPrintTime = queryParams.value.printStartTime && queryParams.value.printEndTime

  if (!hasLabelTime && !hasPrintTime) {
    ElMessage.warning('请选择标签日期或打印时间点')
    return
  }

  if (searchMode.value === 'single') {
    if (queryParams.value.labelNumber === null || queryParams.value.labelNumber === '') {
      ElMessage.warning('请输入标签号')
      return
    }
    
    // 验证是否为有效整数
    if (!isValidInteger(queryParams.value.labelNumber)) {
      ElMessage.warning('标签号必须是有效的整数')
      return
    }
    
    // 清空范围搜索参数
    queryParams.value.labelNumberStart = null
    queryParams.value.labelNumberEnd = null
    
  } else if (searchMode.value === 'range') {
    if (rangeStart.value === null || rangeEnd.value === null) {
      ElMessage.warning('请输入完整的标签号范围')
      return
    }
    
    // 验证是否为有效整数
    if (!isValidInteger(rangeStart.value) || !isValidInteger(rangeEnd.value)) {
      ElMessage.warning('标签号范围必须是有效的整数')
      return
    }
    
    // 校验范围逻辑
    if (rangeStart.value >= rangeEnd.value) {
      ElMessage.warning('起始标签号必须小于结束标签号')
      return
    }
  }

  proxy.download('extra/mes/report/bydlabelprint/export', {
    ...queryParams.value
  }, `bydlabelprint_${new Date().getTime()}.xlsx`)
}

//getList()

// defineExpose({
//   getList
// })
</script>