<template>
  <div class="app-container tyco-report">
    <!-- 原页面标题保留，当前使用菜单导航展示页面名称。
    <h2>泰科装箱追溯报表</h2>
    -->
    <el-form :inline="true" :model="query" @submit.prevent="search">
      <el-form-item label="客户料号"><el-input v-model="query.part" clearable placeholder="输入料号" @keyup.enter="search" /></el-form-item>
      <el-form-item label="内箱码"><el-input v-model="query.innerCode" clearable placeholder="输入内箱标识或箱码" @keyup.enter="search" /></el-form-item>
      <el-form-item label="外箱码"><el-input v-model="query.outerCode" clearable placeholder="输入当前关联外箱码" @keyup.enter="search" /></el-form-item>
      <el-form-item label="产品SN"><el-input v-model="query.productCode" clearable placeholder="输入当前有效产品 SN" @keyup.enter="search" /></el-form-item>
      <el-form-item label="状态">
        <el-select v-model="query.status" clearable placeholder="全部状态" style="width: 145px">
          <el-option v-for="(label,value) in statuses" :key="value" :label="label" :value="value" />
        </el-select>
      </el-form-item>
      <el-form-item label="是否尾箱">
        <el-select v-model="query.tailBox" clearable placeholder="全部" style="width: 105px">
          <el-option label="是" :value="true" /><el-option label="否" :value="false" />
        </el-select>
      </el-form-item>
<!--      <el-form-item label="装箱完成日期">-->
<!--        <el-date-picker v-model="dates" type="daterange" value-format="YYYY-MM-DD" range-separator="至"-->
<!--                        start-placeholder="开始日期" end-placeholder="结束日期" style="width: 260px" />-->
<!--      </el-form-item>-->
      <el-form-item>
        <el-button type="primary" icon="Search" @click="search">查询</el-button>
        <el-button icon="Refresh" @click="reset">重置</el-button>
        <el-button type="warning" plain icon="Download" :loading="exporting" :disabled="exporting" @click="exportReport">导出</el-button>
      </el-form-item>
    </el-form>
<!--    <p class="report-note">每行对应一次已完成的内箱装箱，包含后续拆箱记录。列表显示当前或最近关联的外箱，历史关联及产品 SN 可在详情查看。</p>-->
    <el-table v-loading="loading" :data="rows" border stripe row-key="id" max-height="650"
              empty-text="暂无符合条件的有效记录">
      <el-table-column type="index" label="序号" width="65" :index="index => (query.pageNum - 1) * query.pageSize + index + 1" />
      <el-table-column prop="outerCode" label="外箱码" min-width="300" show-overflow-tooltip />
      <el-table-column prop="part" label="客户料号" min-width="135" show-overflow-tooltip />
      <el-table-column prop="rawCode" label="内箱码" min-width="260" show-overflow-tooltip />
      <el-table-column prop="quantity" label="标签数量" width="95" />
      <el-table-column label="实际数量" width="95"><template #default="{row}">{{ actualQuantity(row) }}</template></el-table-column>
      <el-table-column label="尾箱" width="70"><template #default="{row}">{{ row.tailBox ? '是' : '否' }}</template></el-table-column>
      <el-table-column label="内箱状态" width="120"><template #default="{row}">{{ statuses[row.status] || row.status }}</template></el-table-column>
      <el-table-column prop="outerTraceNo" label="外箱追溯码" min-width="180" show-overflow-tooltip />
      <!-- 完整二维码与 LT 追溯码分别展示，长码悬停查看。 -->
      <el-table-column label="外箱关联" width="105"><template #default="{row}">{{ row.bindingActive ? '已关联' : '未关联' }}</template></el-table-column>
      <el-table-column prop="outerQuantity" label="外箱数量" width="95" />
      <el-table-column prop="reviewedBy" label="装箱确认人" min-width="110" />
      <el-table-column label="装箱完成时间" min-width="165"><template #default="{row}">{{ time(row.reviewedAt) }}</template></el-table-column>
      <!-- 固定显示关联时间，宽表横向滚动时仍可直接查看。 -->
      <el-table-column prop="boundAt" label="内外箱关联时间" width="175" fixed="right"><template #default="{row}">{{ time(row.boundAt) }}</template></el-table-column>
      <el-table-column label="操作" width="125" fixed="right"><template #default="{row}"><el-button link type="primary" @click="openDetail(row)">关联与SN明细</el-button></template></el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="load" />

    <el-dialog v-model="detailOpen" title="装箱追溯明细" width="min(1200px, 95vw)" :close-on-click-modal="false">
      <div v-loading="detailLoading" class="detail-content">
        <template v-if="detail">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="客户料号">{{ detail.box.part }}</el-descriptions-item>
            <el-descriptions-item label="内箱状态">{{ statuses[detail.box.status] }}</el-descriptions-item>
            <el-descriptions-item label="内箱码" :span="2">{{ detail.box.rawCode }}</el-descriptions-item>
            <!-- 标签数量保留在主表，明细只展示实际装箱数量。 -->
            <!-- <el-descriptions-item label="标签数量">{{ detail.box.quantity }}</el-descriptions-item> -->
            <el-descriptions-item label="实际装箱数量">{{ actualQuantity(detail.box) }}</el-descriptions-item>
            <el-descriptions-item label="是否尾箱">{{ detail.box.tailBox ? '是' : '否' }}</el-descriptions-item>
            <el-descriptions-item label="装箱确认人">{{ detail.box.reviewedBy || '—' }}</el-descriptions-item>
            <el-descriptions-item label="装箱完成时间">{{ time(detail.box.reviewedAt) }}</el-descriptions-item>
          </el-descriptions>
          <el-divider>当前外箱关联</el-divider>
          <el-table :data="detail.bindings" border max-height="260" empty-text="暂无有效外箱关联">
            <el-table-column prop="outerCode" label="完整外箱码" min-width="350" show-overflow-tooltip />
            <el-table-column prop="traceNo" label="物料追溯码" min-width="175" />
            <el-table-column prop="quantity" label="外箱数量" width="95" />
            <el-table-column label="状态" width="90"><template #default>已关联</template></el-table-column>
            <el-table-column label="关联时间" min-width="165"><template #default="{row}">{{ time(row.boundAt) }}</template></el-table-column>
          </el-table>
          <el-divider>当前产品 SN</el-divider>
<!--          <p class="report-note">保留移除、拆箱后的扫描历史。占用状态反映本次记录当前是否有效。</p>-->
          <el-table :data="visibleScans" border max-height="330">
            <el-table-column label="序号" width="65"><template #default="{ $index }">{{ (snPage - 1) * 20 + $index + 1 }}</template></el-table-column>
            <el-table-column prop="productCode" label="产品 SN" min-width="230" />
            <el-table-column label="扫描时间" min-width="165"><template #default="{row}">{{ time(row.scannedAt) }}</template></el-table-column>
          </el-table>
          <el-pagination class="sn-pagination" v-model:current-page="snPage" :page-size="20" :total="detail.scans.length" layout="total, prev, pager, next" />
        </template>
      </div>
    </el-dialog>
  </div>
</template>

<script setup name="TycoPackingReport">
import { computed, getCurrentInstance, onMounted, reactive, ref } from 'vue'
import { listTycoPackingReport, getTycoPackingReport } from '@/api/extra/tyco_packing_report'

// PC 主表由数据库分页，扫描和当前外箱关联在点击详情后展示。
const { proxy } = getCurrentInstance()
// 历史状态选项包含 UNBOUND（已拆箱），当前报表只展示有效装箱记录。
const statuses = { PACKED: '已装箱', BOUND: '已关联外箱' }
// 报表主列表默认每页 10 条，历史默认值为 pageSize: 20。
const query = reactive({ pageNum: 1, pageSize: 10, part: '', innerCode: '', outerCode: '', productCode: '', status: '', tailBox: undefined })
const dates = ref([]), rows = ref([]), total = ref(0), loading = ref(false)
const exporting = ref(false)
const detailOpen = ref(false), detailLoading = ref(false), detail = ref(null), snPage = ref(1)
let listRequest = 0, detailRequest = 0
const visibleScans = computed(() => detail.value?.scans.slice((snPage.value - 1) * 20, snPage.value * 20) || [])
const time = value => value ? proxy.parseTime(value) : '—'
// 有效旧内箱没有实际数量快照时沿用标签数量。
const actualQuantity = row => row.packedQuantity ?? row.quantity

// 列表和导出共用筛选条件，导出参数独立于当前页码。
function reportFilters() {
  return {
    part: query.part, innerCode: query.innerCode, outerCode: query.outerCode,
    productCode: query.productCode, status: query.status,
    tailBox: typeof query.tailBox === 'boolean' ? query.tailBox : undefined,
    beginDate: dates.value?.[0], endDate: dates.value?.[1]
  }
}

async function exportReport() {
  if (exporting.value) return
  exporting.value = true
  try {
    await proxy.download('extra/mes/report/mes_tyco_scan/packing/report/export', reportFilters(),
      `泰科装箱追溯报表_${Date.now()}.xlsx`, { timeout: 120000 })
  } finally {
    exporting.value = false
  }
}

async function load() {
  const request = ++listRequest
  loading.value = true
  try {
    const result = await listTycoPackingReport({ ...reportFilters(), pageNum: query.pageNum, pageSize: query.pageSize })
    if (request !== listRequest) return
    // 直接使用服务端过滤结果，保证列表、总数和导出范围一致。
    rows.value = result.rows
    total.value = result.total
  } catch {
    if (request === listRequest) { rows.value = []; total.value = 0 }
  } finally {
    if (request === listRequest) loading.value = false
  }
}
function search() { query.pageNum = 1; load() }
function reset() {
  Object.assign(query, { pageNum: 1, part: '', innerCode: '', outerCode: '', productCode: '', status: '', tailBox: undefined })
  dates.value = []
  load()
}
async function openDetail(row) {
  const request = ++detailRequest
  detail.value = null
  detailOpen.value = true
  detailLoading.value = true
  snPage.value = 1
  try {
    const result = await getTycoPackingReport(row.id)
    if (request !== detailRequest) return
    const record = result.data
    if (!record) {
      detailOpen.value = false
      return
    }
    // 兼容仍返回历史记录的后端版本，先筛选有效明细，再计算 SN 总数和分页。
    detail.value = {
      ...record,
      scans: (record.scans || []).filter(scan => scan.active === true),
      bindings: (record.bindings || []).filter(binding => binding.active === true)
    }
  } catch {
    if (request === detailRequest) detailOpen.value = false
  } finally {
    if (request === detailRequest) detailLoading.value = false
  }
}
onMounted(load)
</script>

<style scoped>
/* PC 宽表横向滚动，查询区域自动换行，长码通过表格提示展示。 */
.tyco-report { min-height: 100vh; background: var(--el-bg-color); }
/* 原标题样式保留：.tyco-report h2 { margin: 0 0 20px; font-size: 22px; } */
.report-note { margin: 12px 0; color: var(--el-text-color-secondary); font-size: 13px; }
.detail-content { min-height: 180px; }
.detail-content :deep(.el-descriptions__content) { overflow-wrap: anywhere; }
.sn-pagination { justify-content: flex-end; margin-top: 16px; }
</style>
