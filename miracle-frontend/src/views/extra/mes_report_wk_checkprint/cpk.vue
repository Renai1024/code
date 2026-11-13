<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" label-width="auto" :rules="rules" class="mb8">
      <el-form-item label="生产工单号" prop="workOrderNo" required>
        <el-input
          v-model="queryParams.workOrderNo"
          placeholder="请输入生产工单号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备编号" prop="deviceNo">
        <el-input
          v-model="queryParams.deviceNo"
          placeholder="请输入设备编号（可选）"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="测试时间范围" prop="testTimeRange">
        <el-date-picker
          v-model="testTimeRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          @change="handleDateRangeChange"
          clearable
          :unlink-panels="true"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">计算CPK</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <div v-if="cpkResult" class="result-container">
      <el-card title="CPK分析报告" class="mb8">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="生产工单号">
                <span class="value-text">{{ cpkResult.workOrderNo || '-' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="设备编号">
                <span class="value-text">{{ cpkResult.deviceNo || '-' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="控制标准上限">
                <span class="value-text">{{ cpkResult.controlUpperLimit || '-' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="有效样本数量">
                <span class="value-text">{{ cpkResult.sampleCount || 0 }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="合格数量">
                <span class="value-text success">{{ cpkResult.qualifiedCount || 0 }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="不合格数量">
                <span class="value-text error">{{ cpkResult.unqualifiedCount || 0 }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="合格率">
                <span class="value-text">{{ cpkResult.qualifiedRate ? cpkResult.qualifiedRate + '%' : '-' }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </el-col>
          <el-col :span="12">
            <el-descriptions :column="2" border>
              <el-descriptions-item label="实测样本均值">
                <span class="value-text">{{ cpkResult.meanValue || '-' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="实测样本标准差">
                <span class="value-text">{{ cpkResult.stdDeviation || '-' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="最小值">
                <span class="value-text">{{ cpkResult.minValue || '-' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="最大值">
                <span class="value-text">{{ cpkResult.maxValue || '-' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="CPK值" :span="2">
                <span :class="getCpkClass(cpkResult.cpkValue)">{{ cpkResult.cpkValue || '-' }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </el-col>
        </el-row>

        <div v-if="cpkResult.maxConsecutiveDefects > 0" class="mt8">
          <el-card title="最大连续不良分析" :body-style="{ padding: '10px' }">
            <el-descriptions :column="3" border>
              <el-descriptions-item label="最大连续不良数">
                <span class="value-text error">{{ cpkResult.maxConsecutiveDefects }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="开始时间">
                <span class="value-text">{{ formatDateTime(cpkResult.maxConsecutiveDefectsStartTime) || '-' }}</span>
              </el-descriptions-item>
              <el-descriptions-item label="结束时间">
                <span class="value-text">{{ formatDateTime(cpkResult.maxConsecutiveDefectsEndTime) || '-' }}</span>
              </el-descriptions-item>
            </el-descriptions>
          </el-card>
        </div>

        <div v-if="cpkResult.cpkValue !== undefined && cpkResult.cpkValue !== null" class="mt8">
          <el-card title="CPK等级评价" :body-style="{ padding: '10px' }">
            <div class="cpk-rating">
              <div v-if="cpkResult.cpkValue >= 1.67" class="rating excellent">
                <span class="rating-icon">✓✓</span>
                <span class="rating-text">能力充足 (CPK ≥ 1.67)</span>
                <!-- <span class="rating-desc">能力充足</span> -->
              </div>
              <div v-else-if="cpkResult.cpkValue >= 1.33" class="rating good">
                <span class="rating-icon">✓</span>
                <span class="rating-text">合格 (1.33 ≤ CPK < 1.67)</span>
                <!-- <span class="rating-desc">合格</span> -->
              </div>
              <div v-else-if="cpkResult.cpkValue >= 1.0" class="rating warning">
                <span class="rating-icon">✗✗</span>
                <span class="rating-text">不足，需改善 (1.0 ≤ CPK < 1.33)</span>
                <!-- <span class="rating-desc">过程能力勉强满足，需加强控制</span> -->
              </div>
              <div v-else class="rating danger">
                <span class="rating-icon">✗</span>
                <span class="rating-text">不可接受 (CPK < 1.0)</span>
                <!-- <span class="rating-desc">过程能力不足，需立即改进</span> -->
              </div>
            </div>
          </el-card>
        </div>

        <div v-if="!cpkResult.sampleCount || cpkResult.sampleCount < 2" class="mt8">
          <el-alert
            title="提示"
            :message="!cpkResult.sampleCount ? '未找到有效数据，请检查查询条件' : '有效样本数不足2个，无法计算CPK'"
            type="warning"
            show-icon
          />
        </div>
      </el-card>
    </div>

    <div v-if="!cpkResult && !loading" class="empty-state">
      <el-empty description="请输入查询条件并点击「计算CPK」按钮" />
    </div>
  </div>
</template>

<script setup name="MesReportWKCheckPrintCPK">
import { ref, reactive, toRefs } from "vue";
import { getCurrentInstance } from "vue";
import { calculateCpkByWorkOrder } from "@/api/extra/mes_report_flaccheckh";
import { ElMessage } from "element-plus";

const { proxy } = getCurrentInstance();

const cpkResult = ref(null);
const loading = ref(false);
const testTimeRange = ref([]);

const data = reactive({
  queryParams: {
    workOrderNo: null,
    deviceNo: null,
    startTime: null,
    endTime: null
  }
});

const rules = {
  workOrderNo: [{ required: true, trigger: "blur", message: "请输入工单号" }]
}

const { queryParams } = toRefs(data);

function handleDateRangeChange(range) {
  if (range && range.length === 2) {
    queryParams.value.startTime = range[0];
    queryParams.value.endTime = range[1];
  } else {
    queryParams.value.startTime = null;
    queryParams.value.endTime = null;
  }
}

function handleQuery() {
  if (!queryParams.value.workOrderNo) {
    ElMessage.warning('请输入生产工单号');
    return;
  }

  loading.value = true;
  calculateCpkByWorkOrder(queryParams.value).then(response => {
    cpkResult.value = response;
    loading.value = false;
  }).catch(() => {
    loading.value = false;
    ElMessage.error('查询失败，请稍后重试');
  });
}

function resetQuery() {
  proxy.resetForm("queryRef");
  testTimeRange.value = [];
  queryParams.value.startTime = null;
  queryParams.value.endTime = null;
  cpkResult.value = null;
}

function getCpkClass(cpkValue) {
  if (cpkValue >= 1.67) return 'cpk-excellent';
  if (cpkValue >= 1.33) return 'cpk-good';
  if (cpkValue >= 1.0) return 'cpk-warning';
  return 'cpk-danger';
}

function formatDateTime(dateStr) {
  if (!dateStr) return null;
  // 处理后端返回的日期格式，可能是 ISO 8601 格式或其他格式
  const date = new Date(dateStr);
  if (isNaN(date.getTime())) return null;
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}
</script>

<style scoped>
.mb8 {
  margin-bottom: 8px;
}

.mt8 {
  margin-top: 8px;
}

.result-container {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.value-text {
  font-weight: 600;
  color: #303133;
}

.value-text.success {
  color: #67c23a;
}

.value-text.error {
  color: #f56c6c;
}

.cpk-rating {
  padding: 15px;
}

.rating {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;
  border-radius: 8px;
}

.rating.excellent {
  background-color: #e8f5e9;
  border: 1px solid #c8e6c9;
}

.rating.good {
  background-color: #e3f2fd;
  border: 1px solid #bbdefb;
}

.rating.warning {
  background-color: #fff8e1;
  border: 1px solid #ffe082;
}

.rating.danger {
  background-color: #ffebee;
  border: 1px solid #ffcdd2;
}

.rating-icon {
  font-size: 32px;
  margin-bottom: 10px;
}

.rating-text {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 5px;
}

.rating-desc {
  font-size: 14px;
  color: #606266;
}

.cpk-excellent {
  font-size: 24px;
  font-weight: 700;
  color: #67c23a;
}

.cpk-good {
  font-size: 24px;
  font-weight: 700;
  color: #409eff;
}

.cpk-warning {
  font-size: 24px;
  font-weight: 700;
  color: #e6a23c;
}

.cpk-danger {
  font-size: 24px;
  font-weight: 700;
  color: #f56c6c;
}

.empty-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}
</style>