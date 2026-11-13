<template>
  <div class="app-container">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>双码绑定</span>
        </div>
      </template>

      <el-row :gutter="20" class="section-row">
        <el-col :span="24">
          <el-form-item label="产品料号" prop="productCode">
            <el-select
              v-model="form.productCode"
              placeholder="请选择产品料号"
              filterable
              clearable
              @change="handleProductCodeChange"
              style="width: 100%;"
            >
              <el-option
                v-for="product in productList"
                :key="product.productCode"
                :label="product.productCode + ' - ' + product.productName"
                :value="product.productCode"
              />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="section-row">
        <el-col :span="24">
          <el-form-item label="设备编号" prop="deviceNo">
            <el-input
              v-model="form.deviceNo"
              placeholder="请输入设备编号"
              clearable
              @blur="saveDeviceNo"
              style="width: 100%;"
            />
            <el-text v-if="hasSavedDeviceNo" type="info" class="ml-2">
              <el-icon><CircleCheck /></el-icon>
              已保存到本地
            </el-text>
          </el-form-item>
        </el-col>
      </el-row>

      <el-divider v-if="form.productCode" />

      <el-row :gutter="20" v-if="form.productCode" class="section-row">
        <el-col :span="24">
          <el-form-item label="产品二维码">
            <div class="scan-input-container">
              <el-input
                v-model="form.productQrCode"
                placeholder="请扫描或输入产品二维码"
                clearable
                @keyup.enter="handleProductQrScan"
                :disabled="isBinding || productQrValidated"
                ref="productQrInputRef"
                class="scan-input"
              >
                <template #append>
                  <el-button
                    @click="handleProductQrScan"
                    :loading="validatingProductQr"
                    :disabled="productQrValidated || !form.deviceNo"
                    size="large"
                  >
                    <el-icon><Search /></el-icon>
                    {{ productQrValidated ? '已验证' : '验证' }}
                  </el-button>
                </template>
              </el-input>
            </div>
            <el-tag v-if="productQrValidated" type="success" class="mt-2">
              <el-icon><CircleCheck /></el-icon>
              验证通过
            </el-tag>
            <el-tag v-if="!form.deviceNo && form.productCode" type="warning" class="mt-2">
              <el-icon><Warning /></el-icon>
              请先输入设备编号
            </el-tag>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20" v-if="form.productCode" class="section-row">
        <el-col :span="24">
          {{form.productQrCode}}
        </el-col>
      </el-row>
      <el-row :gutter="20" v-if="form.productCode" class="section-row">
        <el-col :span="24">
          <el-form-item label="扩展码">
            <div class="scan-input-container">
              <el-input
                v-model="form.extensionCode"
                placeholder="请扫描或输入扩展码"
                clearable
                @keyup.enter="handleExtensionScan"
                :disabled="isBinding || extensionValidated"
                ref="extensionInputRef"
                class="scan-input"
              >
                <template #append>
                  <el-button
                    @click="handleExtensionScan"
                    :loading="validatingExtension"
                    :disabled="extensionValidated"
                    size="large"
                  >
                    <el-icon><Search /></el-icon>
                    {{ extensionValidated ? '已验证' : '验证' }}
                  </el-button>
                </template>
              </el-input>
            </div>
            <el-tag v-if="extensionValidated" type="success" class="mt-2">
              <el-icon><CircleCheck /></el-icon>
              验证通过
            </el-tag>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="20" v-if="form.productCode" class="section-row">
        <el-col :span="24">
          {{form.extensionCode}}
        </el-col>
      </el-row>
      <el-row :gutter="20" v-if="form.productCode" class="button-row">
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

      <el-result
        v-if="bindResult.show"
        :icon="bindResult.success ? 'success' : 'error'"
        :title="bindResult.title"
        :sub-title="bindResult.message"
      >
        <template #extra>
          <el-button type="primary" size="large" @click="handleReset">继续绑定</el-button>
        </template>
      </el-result>
    </el-card>

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

      <div class="records-container" v-infinite-scroll="loadMoreRecords" :infinite-scroll-disabled="loadingMore" :infinite-scroll-distance="100">
        <el-card
          v-for="record in records"
          :key="record.bindUuid"
          class="record-card"
        >
          <template #header>
            <div class="record-card-header">
              <span class="record-title">绑定记录 #{{ record.id }}</span>
              <el-tag size="small" type="success">已绑定</el-tag>
            </div>
          </template>
          <div class="record-card-content">
            <div class="record-info">
              <span><strong>产品料号：</strong>{{ record.productCode }}</span>
              <span><strong>产品二维码：</strong>{{ record.barcodeValue1 }}</span>
              <span><strong>扩展码：</strong>{{ record.barcodeValue2 }}</span>
              <span><strong>设备编号：</strong>{{ record.deviceNo || '-' }}</span>
              <span><strong>操作人：</strong>{{ record.createBy }}</span>
              <span><strong>绑定时间：</strong>{{ parseTime(record.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
            </div>
          </div>
        </el-card>
        <div v-if="loadingMore" class="loading-more">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>加载中...</span>
        </div>
        <div v-if="!loadingMore && records.length === 0" class="no-data">
          <el-empty description="暂无绑定记录" />
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup name="BarcodeBind">
import { ref, reactive, computed, nextTick, onMounted, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getProductList,
  validateBarcode,
  bindBarcode,
  listBarcodeBind
} from '@/api/extra/barcode_bind.js'

const { proxy } = getCurrentInstance()

const form = reactive({
  productCode: '',
  deviceNo: '',
  productQrCode: '',
  extensionCode: ''
})

const productList = ref([])
const validatingProductQr = ref(false)
const validatingExtension = ref(false)
const productQrValidated = ref(false)
const extensionValidated = ref(false)
const isBinding = ref(false)
const hasSavedDeviceNo = ref(false)

const bindResult = reactive({
  show: false,
  success: false,
  title: '',
  message: ''
})

const pageNum = ref(1)
const pageSize = ref(10)
const hasMore = ref(true)
const loadingRecords = ref(false)
const loadingMore = ref(false)
const records = ref([])

const canBind = computed(() => {
  return !bindResult.show &&
         form.productCode &&
         form.deviceNo &&
         form.productQrCode &&
         form.extensionCode &&
         productQrValidated.value &&
         extensionValidated.value
})

function loadDeviceNo() {
  try {
    const saved = localStorage.getItem('barcodeBindDeviceNo')
    if (saved) {
      form.deviceNo = saved
      hasSavedDeviceNo.value = true
    }
  } catch (error) {
    console.error('加载设备编号失败:', error)
  }
}

function saveDeviceNo() {
  if (form.deviceNo) {
    try {
      localStorage.setItem('barcodeBindDeviceNo', form.deviceNo)
      hasSavedDeviceNo.value = true
      ElMessage.success('设备编号已保存到本地')
    } catch (error) {
      console.error('保存设备编号失败:', error)
    }
  }
}

async function loadProducts() {
  try {
    const res = await getProductList()
    if (res.code === 200) {
      productList.value = res.data || []
    } else {
      ElMessage.error('加载产品列表失败')
    }
  } catch (error) {
    console.error('加载产品列表失败:', error)
    ElMessage.error('加载产品列表失败')
  }
}

function handleProductCodeChange() {
  form.productQrCode = ''
  form.extensionCode = ''
  productQrValidated.value = false
  extensionValidated.value = false
}

async function handleProductQrScan() {
  if (!form.productQrCode) {
    ElMessage.warning('请输入产品二维码')
    return
  }
  if (!form.deviceNo) {
    ElMessage.warning('请先输入设备编号')
    return
  }

  validatingProductQr.value = true
  try {
    const res = await validateBarcode({
      barcodeType: 'PRODUCT_QR_CODE',
      barcode: form.productQrCode,
      productCode: form.productCode,
      deviceNo: form.deviceNo
    })
    if (res.code === 200) {
      productQrValidated.value = true
      ElMessage.success('产品二维码验证通过')
      nextTick(() => {
        proxy.$refs.extensionInputRef?.focus()
      })
    } else {
      ElMessageBox.alert(res.msg || '产品二维码验证失败', '错误')
      productQrValidated.value = false
    }
  } catch (error) {
    ElMessageBox.alert('产品二维码验证失败：' + (error.message || '未知错误'), '错误')
    productQrValidated.value = false
  } finally {
    validatingProductQr.value = false
  }
}

async function handleExtensionScan() {
  if (!form.extensionCode) {
    ElMessage.warning('请输入扩展码')
    return
  }

  validatingExtension.value = true
  try {
    const res = await validateBarcode({
      barcodeType: 'EXTENSION_CODE',
      barcode: form.extensionCode
    })
    if (res.code === 200) {
      extensionValidated.value = true
      ElMessage.success('扩展码验证通过')
      if (canBind.value) {
        handleBind()
      }
    } else {
      ElMessage.error(res.msg || '扩展码验证失败')
      extensionValidated.value = false
    }
  } catch (error) {
    ElMessage.error('扩展码验证失败：' + (error.message || '未知错误'))
    extensionValidated.value = false
  } finally {
    validatingExtension.value = false
  }
}

async function handleBind() {
  if (!canBind.value) {
    if (!form.productCode) {
      ElMessage.warning('请选择产品料号')
    } else if (!form.deviceNo) {
      ElMessage.warning('请输入设备编号')
    } else if (!form.productQrCode) {
      ElMessage.warning('请扫描产品二维码')
    } else if (!form.extensionCode) {
      ElMessage.warning('请扫描扩展码')
    } else if (!productQrValidated.value) {
      ElMessage.warning('请先验证产品二维码')
    } else if (!extensionValidated.value) {
      ElMessage.warning('请先验证扩展码')
    }
    return
  }

  isBinding.value = true
  const bindRequest = {
    productCode: form.productCode,
    productQrCode: form.productQrCode,
    extensionCode: form.extensionCode,
    deviceNo: form.deviceNo
  }

  try {
    const res = await bindBarcode(bindRequest)
    if (res.code === 200) {
      ElMessage.success('绑定成功')
      // 重置表单，自动继续绑定
      handleReset()
      // 将光标跳到产品二维码输入框
      nextTick(() => {
        proxy.$refs.productQrInputRef?.focus()
      })
      // 刷新记录列表
      pageNum.value = 1
      records.value = []
      hasMore.value = true
      loadRecords()
    } else {
      ElMessage.error(res.msg || '绑定失败，请稍后重试')
    }
  } catch (error) {
    ElMessage.error('绑定失败：' + (error.message || '未知错误'))
  } finally {
    isBinding.value = false
  }
}

function handleReset() {
  form.productQrCode = ''
  form.extensionCode = ''
  productQrValidated.value = false
  extensionValidated.value = false
}

async function loadRecords() {
  if (loadingRecords.value || loadingMore.value || !hasMore.value) return

  loadingMore.value = true
  loadingRecords.value = true
  try {
    const res = await listBarcodeBind({
      pageNum: pageNum.value,
      pageSize: pageSize.value
    })
    if (res.code === 200) {
      const data = res.rows || []
      if (pageNum.value === 1) {
        records.value = data
      } else {
        records.value.push(...data)
      }
      hasMore.value = data.length === pageSize.value
      if (hasMore.value) {
        pageNum.value++
      }
    } else {
      hasMore.value = false
      ElMessage.warning(res?.message || '加载失败')
    }
  } catch (error) {
    console.error('加载绑定记录失败:', error)
    ElMessage.error('加载绑定记录失败')
  } finally {
    loadingRecords.value = false
    loadingMore.value = false
  }
}

async function loadMoreRecords() {
  if (loadingRecords.value || loadingMore.value || !hasMore.value) {
    return
  }
  try {
    await loadRecords()
  } finally {
  }
}

onMounted(() => {
  loadDeviceNo()
  loadProducts()
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

.button-row {
  margin-top: 30px;
  padding-top: 20px;
}

.text-center {
  text-align: center;
}

.ml-2 {
  margin-left: 10px;
}

.mt-2 {
  margin-top: 10px;
}

.mt-4 {
  margin-top: 20px;
}

.scan-input {
  width: 100%;
}

.scan-input-container {
  width: 100%;
}

.bind-button {
  min-width: 150px;
  margin-right: 20px;
}

.reset-button {
  min-width: 120px;
}

.records-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 10px 0;
}

.record-card {
  transition: all 0.3s ease;
}

.record-card:hover {
  transform: translateY(-2px);
}

.record-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.record-title {
  font-weight: bold;
  font-size: 14px;
}

.record-card-content {
  margin-top: 10px;
}

.record-info {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  font-size: 14px;
}

.loading-more {
  text-align: center;
  padding: 20px;
}

.no-data {
  padding: 40px 0;
}

@media screen and (max-width: 768px) {
  .app-container {
    padding: 5px;
  }
  
  .card-header {
    font-size: 14px;
  }
  
  .record-info {
    flex-direction: column;
    gap: 5px;
  }
  
  .bind-button,
  .reset-button {
    width: 100%;
    margin-right: 0;
    margin-bottom: 10px;
  }
}

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
  
  .el-input__inner {
    padding: 12px;
  }
}
</style>