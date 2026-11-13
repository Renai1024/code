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
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getCount"></right-toolbar>
    </el-row>

    <div class="statistics-container mb15">
      <el-skeleton 
        v-if="loading" 
        :rows="2" 
        animated
        style="padding: 16px;"
      />
      
      <el-row v-else :gutter="20">
        <el-col :span="8">
          <div 
            class="statistic-card print-statistic clickable" 
            :class="{ 'dark-mode': isDarkMode, 'active': activeTab === 'print' }"
            @click="handleStatClick('print')"
          >
            <div class="statistic-icon">
              <el-icon><Printer /></el-icon>
            </div>
            <div class="statistic-content">
              <div class="statistic-title">标签打印记录</div>
              <div class="statistic-value">{{ formatNumber(printCount) }}</div>
              <div class="statistic-desc">已打印的标签总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div 
            class="statistic-card scan-statistic clickable" 
            :class="{ 'dark-mode': isDarkMode, 'active': activeTab === 'scan' }"
            @click="handleStatClick('scan')"
          >
            <div class="statistic-icon">
              <el-icon><Document /></el-icon>
            </div>
            <div class="statistic-content">
              <div class="statistic-title">标签扫码记录</div>
              <div class="statistic-value">{{ formatNumber(scanCount) }}</div>
              <div class="statistic-desc">已扫码记录总数（包含标签重复扫码次数）</div>
            </div>
          </div>
        </el-col>
        <el-col :span="8">
          <div 
            class="statistic-card unscanned-statistic clickable" 
            :class="{ 'dark-mode': isDarkMode, 'active': activeTab === 'unscanned' }"
            @click="handleStatClick('unscanned')"
          >
            <div class="statistic-icon">
              <el-icon><WarningFilled /></el-icon>
            </div>
            <div class="statistic-content">
              <div class="statistic-title">未扫码记录</div>
              <div class="statistic-value">{{ formatNumber(unscannedCount) }}</div>
              <div class="statistic-desc">已打印但未扫码的标签数量</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    
    <!-- 详细数据表格区域 - 仅当有选中的统计项且数量>0时显示 -->
    <div v-if="showDetailSection" class="detail-section mt15">
      <div class="detail-header">
        <h3 class="detail-title">
          <el-icon v-if="activeTab === 'print'"><Printer /></el-icon>
          <el-icon v-else-if="activeTab === 'scan'"><Document /></el-icon>
          <el-icon v-else-if="activeTab === 'unscanned'"><WarningFilled /></el-icon>
          {{ currentDetailTitle }}
        </h3>
        <div class="detail-actions">
          <el-button 
            type="primary" 
            icon="Download" 
            @click="handleExport"
          >导出</el-button>
        </div>
      </div>
      
      <el-table 
        v-loading="detailLoading" 
        :data="detailList" 
        height="500px"
        stripe
        @selection-change="handleSelectionChange"
      >
        <!-- 标签打印记录详情列 -->
        <template v-if="activeTab === 'print'">
          <el-table-column type="index" width="50" align="center" />
          <el-table-column label="物料号" align="center" prop="materialCode" min-width="100" show-overflow-tooltip />
          <el-table-column label="版本号" align="center" prop="materialVersion" min-width="60" show-overflow-tooltip />
          <el-table-column label="客户编号" align="center" prop="customerCode" min-width="100" show-overflow-tooltip />
          <el-table-column label="电镀标识" align="center" prop="ddfactoryFlag" min-width="80" show-overflow-tooltip />
          <el-table-column label="标签流水号" align="center" prop="labelNumber" min-width="120" show-overflow-tooltip />
          <el-table-column label="标签日期" align="center" prop="manufactureDate" min-width="90" show-overflow-tooltip>
            <template #default="scope">
              {{ parseTime(scope.row.manufactureDate, '{y}-{m}-{d}') }}
            </template>
          </el-table-column>
          <el-table-column label="打印时间点" align="center" prop="labelPrintDateTime" min-width="120" show-overflow-tooltip>
            <template #default="scope">
              {{ parseTime(scope.row.labelPrintDateTime, '{y}-{m}-{d} {h}:{i}:{s}') }}
            </template>
          </el-table-column>
        </template>
        
        <!-- 标签扫码记录详情列 -->
        <template v-else-if="activeTab === 'scan'">
          <el-table-column type="index" width="25" align="center" />
          <el-table-column label="物料号" align="center" prop="materialCode" min-width="80" show-overflow-tooltip/>
      <el-table-column label="版本号" align="center" prop="materialVersion" min-width="45" show-overflow-tooltip/>
      <el-table-column label="客户编号" align="center" prop="customerCode" min-width="70" show-overflow-tooltip/>
      <el-table-column label="电镀标识" align="center" prop="ddfactoryFlag" min-width="80" show-overflow-tooltip/>
      <el-table-column label="条码" align="center" prop="barCodeStr" min-width="240" show-overflow-tooltip/>
      <el-table-column label="标签流水号" align="center" prop="labelNumber" min-width="50" show-overflow-tooltip/>
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
      <el-table-column label="扫码所在电脑名称" align="center" prop="scanMachine" min-width="80" show-overflow-tooltip/>
      <el-table-column label="扫码所在电脑" align="center" prop="scanIPAddress" min-width="90" show-overflow-tooltip/>
      <el-table-column label="是否重码" align="center" prop="duplicateFlag" min-width="40" show-overflow-tooltip/>
        </template>
        
        <!-- 未扫码记录详情列 -->
        <template v-else-if="activeTab === 'unscanned'">
          <el-table-column label="物料号" align="center" prop="materialCode" min-width="100" show-overflow-tooltip />
          <el-table-column label="版本号" align="center" prop="materialVersion" min-width="60" show-overflow-tooltip />
          <el-table-column label="客户编号" align="center" prop="customerCode" min-width="100" show-overflow-tooltip />
          <el-table-column label="电镀标识" align="center" prop="ddfactoryFlag" min-width="80" show-overflow-tooltip />
          <el-table-column label="标签流水号" align="center" prop="labelNumber" min-width="120" show-overflow-tooltip />
          <el-table-column label="标签日期" align="center" prop="manufactureDate" min-width="90" show-overflow-tooltip>
            <template #default="scope">
              {{ parseTime(scope.row.manufactureDate, '{y}-{m}-{d}') }}
            </template>
          </el-table-column>
          <el-table-column label="打印时间点" align="center" prop="labelPrintDateTime" min-width="120" show-overflow-tooltip>
            <template #default="scope">
              {{ parseTime(scope.row.labelPrintDateTime, '{y}-{m}-{d} {h}:{i}:{s}') }}
            </template>
          </el-table-column>
        </template>
      </el-table>
      
      <!-- 分页组件 -->
      <pagination
        v-show="totalCount>0"
        :total="totalCount"
        v-model:page="detailQueryParams.pageNum"
        v-model:limit="detailQueryParams.pageSize"
        @pagination="getDetailList"
      />
    </div>

  </div>
</template>

<script setup name="PrintScanCountTab">
import { 
  getBydLabelPrintScanCount,
  listBydlabelprint,
  listBydlabelprintscan,
  getMesBydLabelPrintUnscannedList
} from "@/api/extra/mes_report_bydlabelprint.js";
import { ref, reactive, toRefs, getCurrentInstance, computed, watch } from 'vue'
import { ElMessage, ElNotification } from "element-plus";
import { parseTime } from "@/utils/ruoyi";

const { proxy } = getCurrentInstance()

const loading = ref(false)
const detailLoading = ref(false)
const showSearch = ref(true)
const labelTimeRange = ref([]);
const printCount = ref(0);
const scanCount = ref(0);
const unscannedCount = ref(0);
const activeTab = ref(null) // 'print', 'scan', 'unscanned'

// 详细数据列表
const detailList = ref([])
const totalCount = ref(0)

// 搜索模式
const searchMode = ref('none')
const rangeStart = ref(null)
const rangeEnd = ref(null)

// 查询参数
const data = reactive({
  form: {},
  queryParams: {
    materialCode: null,
    labelPrintDateTime: null,
    labelNumber: null,
    labelStartTime: null,
    labelEndTime: null,
    labelNumberStart: null,
    labelNumberEnd: null
  }
})

// 详细数据查询参数
const detailQueryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  materialCode: null,
  labelStartTime: null,
  labelEndTime: null,
  labelNumber: null,
  labelNumberStart: null,
  labelNumberEnd: null
})

const { queryParams } = toRefs(data)

// 监听主题变化
const isDarkMode = computed(() => {
  return document.body.classList.contains('dark') || 
         document.body.classList.contains('night-mode') ||
         localStorage.getItem('theme') === 'dark'
})

// 是否显示详细数据区域
const showDetailSection = computed(() => {
  if (!activeTab.value) return false
  
  switch(activeTab.value) {
    case 'print': return printCount.value > 0
    case 'scan': return scanCount.value > 0
    case 'unscanned': return unscannedCount.value > 0
    default: return false
  }
})

// 计算属性：当前详情标题
const currentDetailTitle = computed(() => {
  switch(activeTab.value) {
    case 'print': return '标签打印记录详情'
    case 'scan': return '标签扫码记录详情'
    case 'unscanned': return '未扫码标签记录详情'
    default: return ''
  }
})

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

// 处理统计卡片点击
function handleStatClick(type) {
  // 如果点击的是当前激活的标签，取消激活
  if (activeTab.value === type) {
    activeTab.value = null
    return
  }
  
  // 检查对应统计数据是否为0
  let count = 0
  switch(type) {
    case 'print': count = printCount.value; break
    case 'scan': count = scanCount.value; break
    case 'unscanned': count = unscannedCount.value; break
  }
  
  if (count === 0) {
    ElMessage.warning('暂无数据，无法查看详情')
    return
  }
  
  // 设置激活标签并加载数据
  activeTab.value = type
  detailQueryParams.pageNum = 1
  
  // 复制统计查询条件到详情查询
  Object.assign(detailQueryParams, {
    materialCode: queryParams.value.materialCode,
    labelStartTime: queryParams.value.labelStartTime,
    labelEndTime: queryParams.value.labelEndTime,
    labelNumber: queryParams.value.labelNumber,
    labelNumberStart: queryParams.value.labelNumberStart,
    labelNumberEnd: queryParams.value.labelNumberEnd
  })
  
  getDetailList()
}

// 获取统计数量
function getCount() {
  loading.value = true;
  
  getBydLabelPrintScanCount(queryParams.value).then(response => {
    printCount.value = response.data.printCount || 0;
    scanCount.value = response.data.scanCount || 0;
    unscannedCount.value = response.data.unscannedCount || 0;
    
    // 如果之前有激活的标签，重新加载详情
    if (activeTab.value) {
      getDetailList()
    }
  })
  .finally(() => {
    loading.value = false;
  });
}

// 获取详细列表
function getDetailList() {
  detailLoading.value = true
  
  // 根据类型调用不同的API
  let apiPromise
  switch(activeTab.value) {
    case 'print':
      apiPromise = listBydlabelprint(detailQueryParams)
      break
    case 'scan':
      apiPromise = listBydlabelprintscan(detailQueryParams)
      break
    case 'unscanned':
      apiPromise = getMesBydLabelPrintUnscannedList(detailQueryParams)
      break
    default:
      apiPromise = Promise.resolve({ rows: [], total: 0 })
  }
  
  apiPromise.then(response => {
    detailList.value = response.rows || []
    totalCount.value = response.total || 0
  })
  .catch(error => {
    ElMessage.error(`获取${currentDetailTitle.value}失败: ${error.message}`)
  })
  .finally(() => {
    detailLoading.value = false
  })
}

/** 搜索按钮操作 */
function handleQuery() {
  // 校验必填条件
  if (!queryParams.value.materialCode) {
    ElMessage.warning('请输入物料号')
    return
  }

  const hasLabelTime = queryParams.value.labelStartTime && queryParams.value.labelEndTime

  if (!hasLabelTime) {
    ElMessage.warning('请选择标签日期')
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
  
  // 重置分页和激活状态
  activeTab.value = null
  detailQueryParams.pageNum = 1
  
  // 执行查询
  getCount()
}

function handleSearchModeChange(newMode) {
  // 清空所有标签号相关字段
  queryParams.value.labelNumber = null
  queryParams.value.labelNumberStart = null
  queryParams.value.labelNumberEnd = null
  
  // 清空临时范围变量
  rangeStart.value = null
  rangeEnd.value = null
}

// 多选框选中数据
function handleSelectionChange(selection) {
  // 这里可以添加选中数据的处理逻辑
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef")
  labelTimeRange.value = []
  queryParams.value.labelStartTime = null
  queryParams.value.labelEndTime = null
  queryParams.value.labelNumberStart = null
  queryParams.value.labelNumberEnd = null
  queryParams.value.labelNumber = null
  
  // 重置统计
  printCount.value = 0;
  scanCount.value = 0;
  unscannedCount.value = 0;
  
  // 重置详情
  activeTab.value = null
  detailList.value = []
  totalCount.value = 0
}

/** 导出按钮操作 */
function handleExport() {
  // 导出前校验
  if (!queryParams.value.materialCode) {
    ElMessage.warning('请输入物料号')
    return
  }

  if (!queryParams.value.labelStartTime || !queryParams.value.labelEndTime) {
    ElMessage.warning('请选择标签日期')
    return
  }

  // 根据当前标签类型导出
  let exportUrl = ''
  switch(activeTab.value) {
    case 'print':
      exportUrl = '/extra/mes/report/bydlabelprint/export'
      break
    case 'scan':
      exportUrl = '/extra/mes/report/bydlabelprint/printScan/export'
      break
    case 'unscanned':
      exportUrl = '/extra/mes/report/bydlabelprint/unscanned/export'
      break
    default:
      ElMessage.warning('请选择要导出的数据类型')
      return
  }
  
  proxy.download(exportUrl, {
    ...detailQueryParams
  }, `${activeTab.value}_records_${new Date().getTime()}.xlsx`)
}

// 数字格式化函数
const formatNumber = (value) => {
  if (value === null || value === undefined || isNaN(value)) return '0'
  return Number(value).toLocaleString()
}

// 验证是否为有效整数
function isValidInteger(value) {
  if (value === null || value === '' || isNaN(value)) {
    return false
  }
  
  const num = Number(value)
  return Number.isInteger(num)
}

</script>

<style scoped>
/* 保持原有统计卡片样式不变 */
.statistics-container {
  background: var(--el-fill-color-blank);
  border-radius: 4px;
  padding: 15px 20px;
  margin-bottom: 15px;
  border-left: 4px solid var(--el-color-primary);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

/* 卡片基础样式 */
.statistic-card {
  display: flex;
  align-items: center;
  background: var(--el-bg-color);
  border-radius: 4px;
  box-shadow: var(--el-box-shadow-light);
  padding: 16px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  height: 100%;
  border: 1px solid var(--el-border-color);
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

/* 激活状态样式 */
.statistic-card.active {
  transform: translateY(-2px);
  box-shadow: var(--el-box-shadow);
  border-color: var(--el-color-primary);
  z-index: 1;
}

/* 悬停效果 */
.statistic-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--el-box-shadow);
  z-index: 1;
}

/* 图标容器 */
.statistic-icon {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  margin-right: 15px;
  font-size: 24px;
  color: #fff;
  background: linear-gradient(135deg, var(--el-color-primary) 0%, var(--el-color-primary-light-3) 100%);
  transition: all 0.3s;
}

/* 内容区域 */
.statistic-content {
  flex: 1;
}

.statistic-title {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  font-weight: 500;
  margin-bottom: 5px;
  letter-spacing: 0.5px;
}

.statistic-value {
  font-size: 24px;
  font-weight: bold;
  color: var(--el-text-color-primary);
  margin-bottom: 5px;
  font-feature-settings: "tnum";
}

.statistic-desc {
  font-size: 12px;
  color: var(--el-text-color-secondary);
  line-height: 1.4;
}

/* 扫描统计卡片特殊样式 */
.scan-statistic .statistic-icon {
  background: linear-gradient(135deg, var(--el-color-success) 0%, var(--el-color-success-light-3) 100%);
}

/* 未扫码统计卡片特殊样式 - 橙色警示色 */
.unscanned-statistic .statistic-icon {
  background: linear-gradient(135deg, var(--el-color-warning) 0%, var(--el-color-warning-light-3) 100%);
}

/* 数字格式化和单位 */
.statistic-value::after {
  content: "条";
  margin-left: 4px;
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

/* 深色模式覆盖样式 */
.dark-mode .statistic-card {
  background: var(--el-bg-color-page);
  border-color: var(--el-border-color-darker);
}

.dark-mode .statistic-icon {
  filter: brightness(1.1);
}

.dark-mode .print-statistic .statistic-icon {
  background: linear-gradient(135deg, var(--el-color-primary-dark-2) 0%, var(--el-color-primary-dark-4) 100%);
}

.dark-mode .scan-statistic .statistic-icon {
  background: linear-gradient(135deg, var(--el-color-success-dark-2) 0%, var(--el-color-success-dark-4) 100%);
}

.dark-mode .unscanned-statistic .statistic-icon {
  background: linear-gradient(135deg, var(--el-color-warning-dark-2) 0%, var(--el-color-warning-dark-4) 100%);
}

/* 动画效果 */
.statistic-card {
  opacity: 0;
  transform: translateY(10px);
  animation: fadeInUp 0.5s ease forwards;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 延迟动画 - 三列依次出现 */
.statistic-card:nth-child(1) {
  animation-delay: 0s;
}

.statistic-card:nth-child(2) {
  animation-delay: 0.1s;
}

.statistic-card:nth-child(3) {
  animation-delay: 0.2s;
}

/* 详细数据区域样式 */
.detail-section {
  background: var(--el-bg-color);
  border-radius: 4px;
  padding: 20px;
  box-shadow: var(--el-box-shadow-light);
}

.detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid var(--el-border-color);
}

.detail-title {
  font-size: 18px;
  font-weight: bold;
  color: var(--el-text-color-primary);
  display: flex;
  align-items: center;
  gap: 8px;
}

.detail-actions {
  display: flex;
  gap: 10px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .statistics-container {
    padding: 10px;
  }
  
  .statistic-card {
    padding: 12px;
    flex-direction: column;
    text-align: center;
  }
  
  .statistic-icon {
    margin-right: 0;
    margin-bottom: 10px;
  }
  
  /* 小屏幕下三列变为单列 */
  .el-col {
    width: 100% !important;
    margin-bottom: 10px;
  }
  
  .el-col:last-child {
    margin-bottom: 0;
  }
  
  /* 详细数据标题样式 */
  .detail-title {
    font-size: 16px;
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
}
</style>