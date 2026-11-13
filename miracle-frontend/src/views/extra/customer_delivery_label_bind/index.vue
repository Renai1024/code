<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>客户出货标签与外箱标签绑定</span>
        </div>
      </template>

      <!-- 客户选择 -->
      <el-row :gutter="20" class="section-row">
        <el-col :span="24">
          <el-form :model="form" ref="formRef" :rules="rules" @submit.prevent label-width="100px">
            <el-form-item label="客户" prop="customerCode">
              <el-select
                v-model="form.customerCode"
                placeholder="请选择客户"
                style="width: 100%"
                @change="handleCustomerChange"
                :disabled="isBinding"
              >
                <el-option
                  v-for="item in customerList"
                  :key="item.customerCode"
                  :label="item.customerName + '（' + item.customerCode + '）'"
                  :value="item.customerCode"
                />
              </el-select>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>

      <!-- 外箱标签码输入区域 -->
      <el-row :gutter="20" class="section-row" v-if="form.customerCode">
        <el-col :span="24">
          <el-form label-width="100px" @submit.prevent>
            <el-form-item label="外箱标签码">
              <div class="barcode-input-container">
                <el-input
                  v-model="form.outerBoxBarcode"
                  type="textarea"
                  placeholder="请扫描或输入外箱标签码"
                  clearable
                  @keydown.enter.stop.prevent="handleOuterBoxScan"
                  :disabled="isBinding || !!outerBoxInfo.CustomerProductCode"
                  :rows="2"
                  :autosize="{ minRows: 2, maxRows: 5 }"
                  style="flex: 1; margin-right: 10px;"
                />
                <el-button @click="handleOuterBoxScan" :loading="validatingOuterBox" :disabled="!!outerBoxInfo.CustomerProductCode">
                  <el-icon><Search /></el-icon>
                  {{ outerBoxInfo.CustomerProductCode ? '已验证' : '验证' }}
                </el-button>
              </div>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>

      <!-- 外箱信息展示 -->
      <el-row v-if="outerBoxInfo.CustomerProductCode" class="info-row">
        <el-col :span="24">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="外箱标签客户料号">
              <el-tag type="success" size="large">{{ outerBoxInfo.CustomerProductCode }}</el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>

      <el-divider v-if="outerBoxInfo.CustomerProductCode" />

      <!-- 出货标签条码输入区域 -->
      <el-row :gutter="20" v-if="outerBoxInfo.CustomerProductCode" class="section-row">
        <el-col :span="24">
          <el-form label-width="100px" @submit.prevent>
            <el-form-item label="出货标签码">
              <div class="barcode-input-container">
                <el-input
                  v-model="form.deliveryLabelBarcode"
                  placeholder="请扫描或输入出货标签条码"
                  clearable
                  @keyup.enter="handleDeliveryLabelScan"
                  :disabled="isBinding || !!deliveryLabelInfo.CustomerProductCode"
                  ref="deliveryLabelInputRef"
                  style="flex: 1; margin-right: 10px;"
                />
                <el-button @click="handleDeliveryLabelScan" :loading="validatingDeliveryLabel" :disabled="!!deliveryLabelInfo.CustomerProductCode">
                  <el-icon><Search /></el-icon>
                  {{ deliveryLabelInfo.CustomerProductCode ? '已验证' : '验证' }}
                </el-button>
              </div>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>

      <!-- 出货标签信息展示 -->
      <el-row v-if="deliveryLabelInfo.CustomerProductCode" class="info-row">
        <el-col :span="24">
          <el-descriptions :column="1" border>
            <el-descriptions-item label="出货标签客户料号">
              <el-tag :type="CustomerProductCodeMatch ? 'success' : 'danger'" size="large">
                {{ deliveryLabelInfo.CustomerProductCode }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="校验结果">
              <el-tag :type="CustomerProductCodeMatch ? 'success' : 'danger'" size="large">
                {{ CustomerProductCodeMatch ? '客户料号一致，可绑定' : '客户料号不一致！' }}
              </el-tag>
            </el-descriptions-item>
          </el-descriptions>
        </el-col>
      </el-row>

      <!-- 操作按钮 -->
      <el-row :gutter="20" v-if="outerBoxInfo.CustomerProductCode" class="button-row">
        <el-col :span="24" class="text-center">
          <el-button
            type="primary"
            size="large"
            @click="handleBind"
            :loading="isBinding"
            :disabled="!canBind"
            class="bind-button"
          >
            <el-icon><Connection /></el-icon>
            确认绑定
          </el-button>
          <el-button
            size="large"
            @click="handleReset"
            :disabled="isBinding"
            class="reset-button"
          >
            <el-icon><RefreshLeft /></el-icon>
            重置
          </el-button>
        </el-col>
      </el-row>

      <!-- 绑定结果提示 -->
      <el-result
        v-if="bindResult.show"
        :icon="bindResult.success ? 'success' : 'error'"
        :title="bindResult.title"
        :sub-title="bindResult.message"
      >
        <template #extra>
          <el-button type="primary" size="large" @click="handleContinue">继续绑定</el-button>
        </template>
      </el-result>
    </el-card>

    <!-- 历史记录 -->
    <el-card class="box-card mt-4" v-if="!bindResult.show">
      <template #header>
        <div class="card-header">
          <span>绑定记录</span>
          <el-button type="primary" link @click="loadRecords">
            <el-icon><Refresh /></el-icon>
            刷新
          </el-button>
        </div>
      </template>

      <el-table v-loading="loadingRecords" :data="records" border stripe>
        <el-table-column type="index" label="序号" width="50" align="center" />
        <el-table-column label="客户编码" prop="deviceNo" width="100" align="center" />
        <el-table-column label="客户料号" prop="productCode" width="140" align="center" />
        <el-table-column label="外箱标签码" prop="barcodeValue1" min-width="200" show-overflow-tooltip />
        <el-table-column label="出货标签条码" prop="barcodeValue2" min-width="200" show-overflow-tooltip />
        <el-table-column label="绑定人" prop="createBy" width="100" align="center" />
        <el-table-column label="绑定时间" prop="createTime" width="160" align="center">
          <template #default="scope">
            <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
        <!-- <el-table-column label="操作" width="80" align="center">
          <template #default="scope">
            <el-button type="danger" link @click="handleDeleteRecord(scope.row)">删除</el-button>
          </template>
        </el-table-column> -->
      </el-table>

      <pagination
        v-show="recordsTotal > 0"
        :total="recordsTotal"
        v-model:page="recordsQuery.pageNum"
        v-model:limit="recordsQuery.pageSize"
        @pagination="loadRecords"
      />
    </el-card>
  </div>
</template>

<script setup name="CustomerDeliveryLabelBind">
import { ref, reactive, computed, nextTick, onMounted, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getActiveRules,
} from '@/api/extra/customer_outer_box_rule.js'
import {
  validateOuterBox,
  validateDeliveryLabel,
  bindDeliveryLabel,
  listDeliveryLabelBind,
  delDeliveryLabelBind
} from '@/api/extra/customer_delivery_label_bind.js'

const { proxy } = getCurrentInstance()

// 表单数据
const form = reactive({
  customerCode: '',
  outerBoxBarcode: '',
  deliveryLabelBarcode: ''
})

const rules = reactive({
  customerCode: [{ required: true, message: '请选择客户', trigger: 'change' }]
})

// 客户列表
const customerList = ref([])

// 外箱信息
const outerBoxInfo = reactive({
  CustomerProductCode: ''
})

// 出货标签信息
const deliveryLabelInfo = reactive({
  CustomerProductCode: ''
})

// 状态控制
const validatingOuterBox = ref(false)
const validatingDeliveryLabel = ref(false)
const isBinding = ref(false)
const loadingRecords = ref(false)

// 绑定结果
const bindResult = reactive({
  show: false,
  success: false,
  title: '',
  message: ''
})

// 记录列表
const records = ref([])
const recordsTotal = ref(0)
const recordsQuery = reactive({
  pageNum: 1,
  pageSize: 10
})

// 计算属性：客户料号是否一致
const CustomerProductCodeMatch = computed(() => {
  if (!outerBoxInfo.CustomerProductCode || !deliveryLabelInfo.CustomerProductCode) {
    return false
  }
  return outerBoxInfo.CustomerProductCode === deliveryLabelInfo.CustomerProductCode
})

// 计算属性：是否可以绑定
const canBind = computed(() => {
  return !bindResult.show &&
         outerBoxInfo.CustomerProductCode &&
         deliveryLabelInfo.CustomerProductCode &&
         CustomerProductCodeMatch.value
})

// 加载客户列表
function loadCustomerList() {
  getActiveRules().then(res => {
    customerList.value = res.data || []
  })
}

// 客户选择变化
function handleCustomerChange() {
  resetBarcodes()
}

// 重置条码
function resetBarcodes() {
  form.outerBoxBarcode = ''
  form.deliveryLabelBarcode = ''
  outerBoxInfo.CustomerProductCode = ''
  deliveryLabelInfo.CustomerProductCode = ''
}

// 处理外箱标签码扫描/验证
async function handleOuterBoxScan(event) {
  if (event) {
    event.preventDefault()
  }
  if (!form.customerCode) {
    ElMessage.warning('请先选择客户')
    return
  }
  if (!form.outerBoxBarcode) {
    ElMessage.warning('请输入外箱标签码')
    return
  }

  validatingOuterBox.value = true
  try {
    const res = await validateOuterBox(form.customerCode, form.outerBoxBarcode)
    if (res.code === 200) {
      outerBoxInfo.CustomerProductCode = res.data.CustomerProductCode
      ElMessage.success('外箱标签码验证成功，客户料号：' + outerBoxInfo.CustomerProductCode)
      nextTick(() => {
        proxy.$refs.deliveryLabelInputRef?.focus()
      })
    } else {
      ElMessageBox.alert(res.msg || '外箱标签码验证失败', '错误')
      resetOuterBoxInfo()
    }
  } catch (error) {
    ElMessageBox.alert('外箱标签码验证失败：' + (error.message || '未知错误'), '错误')
    resetOuterBoxInfo()
  } finally {
    validatingOuterBox.value = false
  }
}

// 处理出货标签条码扫描/验证
async function handleDeliveryLabelScan(event) {
  if (event) {
    event.preventDefault()
  }
  if (!form.customerCode) {
    ElMessage.warning('请先选择客户')
    return
  }
  if (!outerBoxInfo.CustomerProductCode) {
    ElMessage.warning('请先验证外箱标签码')
    return
  }
  if (!form.deliveryLabelBarcode) {
    ElMessage.warning('请输入出货标签条码')
    return
  }

  validatingDeliveryLabel.value = true
  try {
    const res = await validateDeliveryLabel(form.customerCode, form.deliveryLabelBarcode)
    if (res.code === 200) {
      deliveryLabelInfo.CustomerProductCode = res.data.CustomerProductCode
      // 校验客户料号是否一致
      if (outerBoxInfo.CustomerProductCode !== deliveryLabelInfo.CustomerProductCode) {
        ElMessageBox.alert(
          `客户料号不一致！\n外箱标签客户料号：${outerBoxInfo.CustomerProductCode}\n出货标签客户料号：${deliveryLabelInfo.CustomerProductCode}`,
          '警告',
          { type: 'error' }
        )
      } else {
        ElMessage.success('出货标签条码验证成功，客户料号一致')
      }
    } else {
      ElMessageBox.alert(res.msg || '出货标签条码验证失败', '错误')
      resetDeliveryLabelInfo()
    }
  } catch (error) {
    ElMessageBox.alert('出货标签条码验证失败：' + (error.message || '未知错误'), '错误')
    resetDeliveryLabelInfo()
  } finally {
    validatingDeliveryLabel.value = false
  }
}

// 处理绑定
async function handleBind() {
  if (!canBind.value) {
    if (!CustomerProductCodeMatch.value) {
      ElMessageBox.alert('客户料号不一致，不允许绑定', '错误')
    }
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要将外箱标签与出货标签进行绑定吗？\n客户料号：${outerBoxInfo.CustomerProductCode}`,
      '确认绑定',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    isBinding.value = true
    const bindRequest = {
      customerCode: form.customerCode,
      outerBoxBarcode: form.outerBoxBarcode,
      deliveryLabelBarcode: form.deliveryLabelBarcode
    }

    const res = await bindDeliveryLabel(bindRequest)
    if (res.code === 200) {
      bindResult.show = true
      bindResult.success = true
      bindResult.title = '绑定成功'
      bindResult.message = `外箱标签与出货标签已成功绑定`
      // 重新加载记录
      recordsQuery.pageNum = 1
      loadRecords()
    } else {
      bindResult.show = true
      bindResult.success = false
      bindResult.title = '绑定失败'
      bindResult.message = res.msg || '绑定失败，请稍后重试'
    }
  } catch (error) {
    if (error !== 'cancel') {
      bindResult.show = true
      bindResult.success = false
      bindResult.title = '绑定失败'
      bindResult.message = error.message || '绑定失败，请稍后重试'
    }
  } finally {
    isBinding.value = false
  }
}

// 继续绑定
function handleContinue() {
  handleReset()
}

// 重置
function handleReset() {
  form.outerBoxBarcode = ''
  form.deliveryLabelBarcode = ''
  resetOuterBoxInfo()
  resetDeliveryLabelInfo()
  bindResult.show = false
  bindResult.success = false
  bindResult.title = ''
  bindResult.message = ''
}

function resetOuterBoxInfo() {
  outerBoxInfo.CustomerProductCode = ''
}

function resetDeliveryLabelInfo() {
  deliveryLabelInfo.CustomerProductCode = ''
}

// 加载绑定记录
function loadRecords() {
  loadingRecords.value = true
  listDeliveryLabelBind(recordsQuery).then(res => {
    records.value = res.rows
    recordsTotal.value = res.total
    loadingRecords.value = false
  }).catch(() => {
    loadingRecords.value = false
  })
}

// 删除记录
// function handleDeleteRecord(row) {
//   ElMessageBox.confirm('确认删除该绑定记录？', '提示', {
//     confirmButtonText: '确定',
//     cancelButtonText: '取消',
//     type: 'warning'
//   }).then(() => {
//     return delDeliveryLabelBind(row.id)
//   }).then(() => {
//     ElMessage.success('删除成功')
//     loadRecords()
//   }).catch(() => {})
// }

// 页面加载
onMounted(() => {
  loadCustomerList()
  loadRecords()
})
</script>

<style scoped>
.app-container {
  padding: 10px;
  min-height: 100vh;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 16px;
  font-weight: bold;
}

.section-row {
  margin-bottom: 20px;
}

.info-row {
  display: block;
  margin-bottom: 20px;
}

.button-row {
  margin-top: 30px;
  padding-top: 20px;
}

.text-center {
  text-align: center;
}

.mt-4 {
  margin-top: 20px;
}

.barcode-input-container {
  display: flex;
  align-items: flex-start;
  width: 100%;
}

.bind-button {
  min-width: 150px;
}

.reset-button {
  min-width: 120px;
}

/* PDA设备优化 */
@media screen and (max-width: 480px) {
  .app-container {
    padding: 2px;
  }

  .el-card {
    margin-bottom: 10px;
  }

  .el-button {
    font-size: 16px;
  }

  .el-input {
    font-size: 16px;
  }
}
</style>
