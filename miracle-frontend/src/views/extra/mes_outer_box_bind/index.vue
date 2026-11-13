<template>
  <div class="app-container" v-hasPermi="['extra:outer_box_bind:groupList']">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>外箱编码绑定产品编码</span>
        </div>
      </template>

      <!-- 外箱编码输入区域 -->
      <el-row :gutter="20" class="section-row">
        <el-col :span="24">
          <el-form :model="form" ref="formRef" :rules="rules" @submit.prevent>
            <el-form-item label="外箱编码" prop="outerBoxBarcode">
              <div class="outer-box-input-container">
                <el-input
                  v-model="form.outerBoxBarcode"
                  type="textarea"
                  placeholder="请扫描或输入外箱编码"
                  clearable
                  @keydown.enter.stop.prevent="handleOuterBoxScan"
                  :disabled="isBinding"
                  :readonly="!!outerBoxInfo.customerPartNumber"
                  :rows="2"
                  :autosize="{ minRows: 2, maxRows: 5 }"
                  style="flex: 1; margin-right: 10px;"
                />
                <el-button v-hasPermi="['extra:outer_box_bind:validateOuterBox']" @click="handleOuterBoxScan" :loading="validatingOuterBox" :disabled="!!outerBoxInfo.customerPartNumber" style="margin-top: 0;">
                  <el-icon><Search /></el-icon>
                  {{ outerBoxInfo.customerPartNumber ? '已验证' : '验证' }}
                </el-button>
              </div>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>

      <!-- 外箱信息展示 -->
      <el-row v-if="outerBoxInfo.customerPartNumber" class="info-row">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="客户料号">{{ outerBoxInfo.customerPartNumber }}</el-descriptions-item>
          <el-descriptions-item label="箱装数量">{{ outerBoxInfo.boxQuantity }}</el-descriptions-item>
          <el-descriptions-item label="已扫码数">
            <el-tag :type="scanCountType" size="large">{{ scannedProducts.length }} / {{ outerBoxInfo.boxQuantity }}</el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </el-row>

      <el-divider v-if="outerBoxInfo.customerPartNumber" />

      <!-- 产品编码扫描区域 -->
      <el-row :gutter="20" v-if="outerBoxInfo.customerPartNumber" class="section-row">
        <el-col :span="24">
          <el-form label-width="70px" @submit.prevent>
            <el-form-item label="产品编码">
              <el-input
                v-model="currentProductBarcode"
                placeholder="请扫描或输入产品编码"
                clearable
                @keyup.enter="handleProductScan"
                :disabled="isBinding || isScanComplete"
                ref="productInputRef"
                class="product-input"
              >
                <template #append>
                  <el-button v-hasPermi="['extra:outer_box_bind:validateProduct']" @click="handleProductScan" :disabled="isScanComplete" size="large">
                    <el-icon><Plus /></el-icon>
                    添加
                  </el-button>
                </template>
              </el-input>
              <el-text v-if="isScanComplete" type="success" class="ml-2">
                <el-icon><CircleCheck /></el-icon>
                扫码完成
              </el-text>
            </el-form-item>
          </el-form>
        </el-col>
      </el-row>

      <!-- 已扫码列表 -->
      <el-row :gutter="20" v-if="scannedProducts.length > 0" class="section-row">
        <el-col :span="24">
          <div class="scanned-list-container">
            <el-table :data="scannedProducts" border stripe :height="tableHeight">
              <el-table-column type="index" label="序号" width="50" align="center" />
              <el-table-column label="产品编码" prop="barcode" min-width="250" show-overflow-tooltip />
              <!-- <el-table-column label="验证状态" width="120" align="center">
                <template #default="scope">
                  <el-tag :type="scope.row.valid ? 'success' : 'danger'">
                    {{ scope.row.valid ? '通过' : '失败' }}
                  </el-tag>
                </template>
              </el-table-column> -->
              <el-table-column label="操作" width="80" align="center" v-if="!isBinding">
                <template #default="scope">
                  <el-button
                    type="danger"
                    link
                    @click="removeProduct(scope.$index)"
                    :disabled="isBinding"
                  >
                    <el-icon><Delete /></el-icon>
                    删除
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-col>
      </el-row>

      <!-- 操作按钮 -->
      <el-row :gutter="20" v-if="outerBoxInfo.customerPartNumber" class="button-row">
        <el-col :span="24" class="text-center">
          <el-button
            v-hasPermi="['extra:outer_box_bind:bind']"
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
            @click="handleReset(0)"
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
          <el-button type="primary" size="large" @click="handleReset(1)">继续绑定</el-button>
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

      <!-- 卡片式布局 -->
      <div class="records-container" v-infinite-scroll="loadMoreRecords" :infinite-scroll-disabled="loadingMore" :infinite-scroll-distance="100">
        <el-card
          v-for="(group, outerBoxBarcode) in groupedRecords"
          :key="outerBoxBarcode"
          class="record-card"
          @click="showDetail(outerBoxBarcode)"
        >
          <template #header>
            <div class="record-card-header">
              <div class="outer-box-barcode">{{ outerBoxBarcode }}</div>
              <el-tag size="small" type="info">{{ group[0]?.productCount || 0 }} 个产品</el-tag>
            </div>
          </template>
          <div class="record-card-content">
            <div class="record-info">
              <span><strong>绑定产品数量：</strong>{{ group[0]?.productCount || 0 }}</span>
              <span><strong>绑定人：</strong>{{ group[0]?.bindBy || '-' }}</span>
              <span><strong>绑定时间：</strong>{{ group[0]?.bindTime ? parseTime(group[0].bindTime, '{y}-{m}-{d} {h}:{i}') : '-' }}</span>
            </div>
            <div class="product-preview">
              <strong>产品编码：</strong>
              <el-tag size="small" class="product-tag">
                {{ group[0]?.firstProductBarcode || '-' }}
              </el-tag>
              <el-text 
                v-if="expandedCard !== outerBoxBarcode"
                type="info" 
                class="ml-2 product-count-text"
                @click.stop="showDetail(outerBoxBarcode)"
              >
                （等{{ group[0]?.productCount || 0 }}个产品）
              </el-text>
              
              <transition name="expand">
                <div v-if="expandedCard === outerBoxBarcode" class="product-list-inline">
                  <el-skeleton :loading="loadingDetails.get(outerBoxBarcode)" animated :rows="3" v-if="loadingDetails.get(outerBoxBarcode)">
                    <template #template>
                      <el-skeleton-item variant="p" style="width: 100%" />
                    </template>
                  </el-skeleton>
                  
                  <div v-else class="product-tags-container">
                    <el-tag 
                      v-for="(product, index) in (productDetails.get(outerBoxBarcode) || []).slice(1)" 
                      :key="index"
                      size="small"
                      class="product-tag-item"
                    >
                      {{ product.productBarcode }}
                    </el-tag>
                    <el-text 
                      size="small" 
                      class="ml-2 product-count-text"
                      @click.stop="expandedCard = ''"
                    >
                      收起
                    </el-text>
                  </div>
                  
                  <div v-if="!loadingDetails.get(outerBoxBarcode) && (!productDetails.get(outerBoxBarcode) || productDetails.get(outerBoxBarcode).length === 0)" class="no-data">
                    <el-empty description="暂无产品编码数据" />
                  </div>
                </div>
              </transition>
            </div>
          </div>
        </el-card>
        <div v-if="loadingMore" class="loading-more">
          <el-icon class="is-loading"><Loading /></el-icon>
          <span>加载中...</span>
        </div>
        <div v-if="!loadingMore && Object.keys(groupedRecords).length === 0" class="no-data">
          <el-empty description="暂无绑定记录" />
        </div>
      </div>
    </el-card>


  </div>
</template>

<script setup name="OuterBoxBind">
import { ref, reactive, computed, nextTick, onMounted, onBeforeUnmount, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import {
  validateOuterBox,
  validateProduct,
  bindOuterBox,
  groupListOuterBoxBind,
  getProductsByOuterBoxBarcode
} from '@/api/extra/mes_outer_box_bind.js'

const { proxy } = getCurrentInstance()

// 表单数据
const form = reactive({
  outerBoxBarcode: ''
})

// 当前产品编码输入
const currentProductBarcode = ref('')

const rules = reactive({
  outerBoxBarcode: [
    { 
      required: true, 
      message: '外箱编码不能为空，请先扫描或输入', 
      trigger: 'blur' 
    }
  ]
})


// 外箱信息
const outerBoxInfo = reactive({
  customerPartNumber: '',
  boxQuantity: 0
})

// 已扫码产品列表
const scannedProducts = ref([])

// 状态控制
const validatingOuterBox = ref(false)
const isBinding = ref(false)
const loadingRecords = ref(false)
const loadingMore = ref(false)

// 绑定结果
const bindResult = reactive({
  show: false,
  success: false,
  title: '',
  message: ''
})

// 分页参数
const pageNum = ref(1)
const pageSize = ref(5)
const hasMore = ref(true)

// 绑定记录
const records = ref([])
const groupedRecords = computed(() => {
  // 由于后端已经按外箱编码分组，直接构建一个以outerBoxBarcode为key的对象
  const groups = {}
  records.value.forEach(record => {
    if (record.outerBoxBarcode) {
      // 后端已经按外箱编码分组，每个外箱编码只会有一条记录
      groups[record.outerBoxBarcode] = [record]
    }
  })
  return groups
})

// 展开的卡片
const expandedCard = ref('')
// 产品编码详情
const productDetails = ref(new Map())
// 加载状态
const loadingDetails = ref(new Map())

// 表格高度
const tableHeight = computed(() => {
  return window.innerHeight > 600 ? '400px' : '300px'
})

// 计算属性：扫码数量状态
const scanCountType = computed(() => {
  const count = scannedProducts.value.length
  const target = outerBoxInfo.boxQuantity
  if (count < target) return 'warning'
  if (count > target) return 'danger'
  return 'success'
})

// 计算属性：是否扫码完成
const isScanComplete = computed(() => {
  return scannedProducts.value.length >= outerBoxInfo.boxQuantity
})

// 计算属性：是否可以绑定
const canBind = computed(() => {
  return !bindResult.show &&
         scannedProducts.value.length === outerBoxInfo.boxQuantity &&
         scannedProducts.value.length > 0 &&
         scannedProducts.value.every(p => p.valid)
})

// 从本地存储加载暂存数据
function loadStoredData() {
  try {
    const storedData = localStorage.getItem('outerBoxBindData')
    if (storedData) {
      const data = JSON.parse(storedData)
      form.outerBoxBarcode = data.outerBoxBarcode || ''
      outerBoxInfo.customerPartNumber = data.customerPartNumber || ''
      outerBoxInfo.boxQuantity = data.boxQuantity || 0
      scannedProducts.value = data.scannedProducts || []
      if (data.scannedProducts && data.scannedProducts.length > 0) {
        ElNotification({
          title: '提示',
          message: '已恢复上次未完成的扫描数据',
          type: 'info'
        })
      }
    }
  } catch (error) {
    console.error('加载暂存数据失败:', error)
  }
}

// 保存数据到本地存储
function saveToLocalStorage() {
  try {
    const data = {
      outerBoxBarcode: form.outerBoxBarcode,
      customerPartNumber: outerBoxInfo.customerPartNumber,
      boxQuantity: outerBoxInfo.boxQuantity,
      scannedProducts: scannedProducts.value
    }
    localStorage.setItem('outerBoxBindData', JSON.stringify(data))
  } catch (error) {
    console.error('保存数据到本地存储失败:', error)
  }
}

// 清除本地存储数据
function clearLocalStorage() {
  localStorage.removeItem('outerBoxBindData')
}

// 处理外箱编码扫描/验证
async function handleOuterBoxScan(event) {
  // 阻止默认的表单提交行为
  if (event) {
    event.preventDefault()
  }
  if (!form.outerBoxBarcode) {
    ElMessage.warning('请输入外箱编码')
    return
  }

  validatingOuterBox.value = true
  try {
    const res = await validateOuterBox(form.outerBoxBarcode)
    if (res.code === 200) {
      outerBoxInfo.customerPartNumber = res.data.customerPartNumber
      outerBoxInfo.boxQuantity = res.data.boxQuantity
      ElMessage.success('外箱编码验证成功')
      // 自动聚焦到产品编码输入框
      nextTick(() => {
        proxy.$refs.productInputRef?.focus()
      })
      // 保存数据
      saveToLocalStorage()
    } else {
      ElMessageBox.alert(res.msg || '外箱编码验证失败','错误')
      //ElMessage.error(res.msg || '外箱编码验证失败')
      resetOuterBoxInfo()
    }
  } catch (error) {
    ElMessageBox.alert('外箱编码验证失败：' + (error.message || '未知错误'),'错误')
    //ElMessage.error('外箱编码验证失败：' + (error.message || '未知错误'))
    resetOuterBoxInfo()
  } finally {
    validatingOuterBox.value = false
  }
}

// 处理产品编码扫描/添加
async function handleProductScan(event) {
  // 阻止默认的表单提交行为
  if (event) {
    event.preventDefault()
  }
  if (!currentProductBarcode.value) {
    ElMessage.warning('请输入产品编码')
    return
  }

  // 检查是否重复扫码
  if (scannedProducts.value.some(p => p.barcode === currentProductBarcode.value)) {
    ElMessage.warning('该产品编码已扫描，请勿重复扫码')
    currentProductBarcode.value = ''
    return
  }

  // 检测条码长度是否一致
  if (scannedProducts.value.length != 0 && currentProductBarcode.value.length != scannedProducts.value[0].barcode.length){
    ElMessage.warning('该产品编码长度与已扫产品编码长度不一致，请核对产品编码')
    return
  }

  // 检查是否超过箱装数量
  if (scannedProducts.value.length >= outerBoxInfo.boxQuantity) {
    ElMessage.warning(`已达到箱装数量(${outerBoxInfo.boxQuantity}个)，请勿继续扫码`)
    currentProductBarcode.value = ''
    return
  }

  try {
    const res = await validateProduct(currentProductBarcode.value)
    const product = {
      barcode: currentProductBarcode.value,
      customerPartNumber: res.data.customerPartNumber,
      valid: res.data.customerPartNumber === outerBoxInfo.customerPartNumber
    }

    if (!product.valid) {
      ElMessageBox.alert(
        `客户料号不匹配！外箱编码客户料号：${outerBoxInfo.customerPartNumber}，产品编码客户料号：${product.customerPartNumber}`,
        '错误'
      )
      currentProductBarcode.value = ''
      return
    }
    ElMessage.success('产品编码验证通过')

    scannedProducts.value.push(product)
    currentProductBarcode.value = ''

    // 保存数据
    saveToLocalStorage()

    // 检查是否扫码完成
    if (scannedProducts.value.length === outerBoxInfo.boxQuantity) {
      // 检查是否所有产品都验证通过
      const allValid = scannedProducts.value.every(p => p.valid)
      if (allValid) {
        ElMessage.success('扫码完成，可以进行绑定操作')
      } else {
        ElMessage.warning('扫码完成，但存在验证失败的产品编码，请删除后重新扫描')
      }
    }
  } catch (error) {
    const errorMsg = error.message || error || '产品编码验证失败'
    currentProductBarcode.value = ''
    ElMessageBox.alert('产品编码验证失败：' + errorMsg, '错误')
    return
    //ElMessage.error('产品编码验证失败：' + (error.message || '未知错误'))
  }
}

// 删除已扫码产品
function removeProduct(index) {
  ElMessageBox.confirm(
    '确认删除？',
    '确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    scannedProducts.value.splice(index, 1)
    ElMessage.success('已删除')
    // 保存数据
    saveToLocalStorage()
  }).catch(() => {
    // 取消重置
  })
}

// 处理绑定
async function handleBind() {
  if (!canBind.value) {
    const count = scannedProducts.value.length
    const target = outerBoxInfo.boxQuantity
    if (count < target) {
      ElMessageBox.alert(`扫码数量不足，还差${target - count}个`,'错误')
      //ElMessage.error(`扫码数量不足，还差${target - count}个`)
    } else if (count > target) {
      ElMessageBox.alert(`扫码数量超出，多出${count - target}个`,'错误')
      //ElMessage.error(`扫码数量超出，多出${count - target}个`)
    } else if (!scannedProducts.value.every(p => p.valid)) {
      ElMessageBox.alert('存在验证失败的产品编码，请删除后重新扫描','错误')
      //ElMessage.error('存在验证失败的产品编码，请删除后重新扫描')
    }
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定要将外箱编码 "${form.outerBoxBarcode}" 与 ${scannedProducts.value.length} 个产品编码进行批量绑定吗？`,
      '确认绑定',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    isBinding.value = true
    const bindRequest = {
      outerBoxBarcode: form.outerBoxBarcode,
      productBarcodes: scannedProducts.value.map(p => p.barcode)
    }

    const res = await bindOuterBox(bindRequest)
    if (res.code === 200) {
      bindResult.show = true
      bindResult.success = true
      bindResult.title = '绑定成功'
      bindResult.message = `外箱编码 "${form.outerBoxBarcode}" 已成功绑定 ${res.data?.bindCount || scannedProducts.value.length} 个产品编码`
      // 清除暂存数据
      clearLocalStorage()
      // 重新加载记录
      pageNum.value = 1
      records.value = []
      hasMore.value = true
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

// 重置
function handleReset(val) {
  var tip = '确定要重置当前操作吗？已扫描的数据将被清除。';
  if (val === 1) {
    tip = '是否确定继续进行绑定。'
  }
  ElMessageBox.confirm(
    tip,
    '确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    form.outerBoxBarcode = ''
    currentProductBarcode.value = ''
    resetOuterBoxInfo()
    scannedProducts.value = []
    bindResult.show = false
    bindResult.success = false
    bindResult.title = ''
    bindResult.message = ''
    // 清除暂存数据
    clearLocalStorage()
    //ElMessage.success('已重置')
  }).catch(() => {
    // 取消重置
  })
}

// 重置外箱信息
function resetOuterBoxInfo() {
  outerBoxInfo.customerPartNumber = ''
  outerBoxInfo.boxQuantity = 0
}

// 加载绑定记录
async function loadRecords() {
  
  if (loadingRecords.value || loadingMore.value || !hasMore.value) return

  
  loadingMore.value = true
  loadingRecords.value = true
  try {
    const res = await groupListOuterBoxBind({
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
    }else {
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

// 加载更多记录
async function loadMoreRecords() {  
  
  if (loadingRecords.value || loadingMore.value || !hasMore.value) {
    return
  }
  
  try {
    await loadRecords()
  } finally {
  }
}

// 显示详情（切换展开状态）
async function showDetail(outerBoxBarcode) {
  // 如果当前卡片已经展开，则收起
  if (expandedCard.value === outerBoxBarcode) {
    expandedCard.value = ''
    return
  }
  
  // 展开新卡片
  expandedCard.value = outerBoxBarcode
  
  // 如果还没有加载过该外箱的产品编码详情，则加载
  if (!productDetails.value.has(outerBoxBarcode)) {
    loadingDetails.value.set(outerBoxBarcode, true)
    try {
      const res = await getProductsByOuterBoxBarcode(outerBoxBarcode)
      if (res.code === 200) {
        // 将产品编号列表转换为对象数组，添加序号
        const products = res.data || []
        const productObjects = products.map((productBarcode, index) => ({
          productBarcode,
          bindSn: index + 1
        }))
        productDetails.value.set(outerBoxBarcode, productObjects)
      } else {
        ElMessageBox.alert('加载详情失败','错误')
        //ElMessage.error('加载详情失败')
      }
    } catch (error) {
      console.error('加载详情失败:', error)
      ElMessageBox.alert('加载详情失败','错误')
      //ElMessage.error('加载详情失败')
    } finally {
      loadingDetails.value.set(outerBoxBarcode, false)
    }
  }
}

// 从外箱编码中提取箱装数量
function extractBoxQuantity(outerBoxBarcode) {
  if (!outerBoxBarcode) return 0
  const match = outerBoxBarcode.match(/Q:(\d+)/)
  return match ? parseInt(match[1]) : 0
}

// 获取箱装数量
function getBoxQuantity(records) {
  if (!records || records.length === 0) return 0
  const outerBoxBarcode = records[0].outerBoxBarcode
  return extractBoxQuantity(outerBoxBarcode) || 0
}

// 页面离开确认
function handleBeforeUnload(event) {
  if (scannedProducts.value.length > 0 && !bindResult.show) {
    event.preventDefault()
    event.returnValue = ''
    return ''
  }
}

// 页面加载时
onMounted(() => {
  // 加载暂存数据
  loadStoredData()
  // nextTick().then(() => {
  //   loadRecords()
  // })
  // 加载绑定记录
  loadRecords()
  // 添加页面离开监听
  window.addEventListener('beforeunload', handleBeforeUnload)
})

// 页面卸载前
onBeforeUnmount(() => {
  // 移除页面离开监听
  window.removeEventListener('beforeunload', handleBeforeUnload)
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
  /* padding: 15px; */
  border-radius: 8px;
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

.mt-4 {
  margin-top: 20px;
}

/* 输入框样式 */
.outer-box-input,
.product-input {
  width: 100%;
  max-width: 500px;
}

/* 外箱编码输入框容器 */
.outer-box-input-container {
  display: flex;
  align-items: flex-start;
  width: 100%;
  max-width: 800px;
}

/* 外箱编码输入框多行显示 */
.outer-box-input-container .el-textarea__inner {
  word-break: break-all;
  white-space: normal;
  line-height: 1.4;
  resize: vertical;
}

/* 只读状态的输入框样式 */
.outer-box-input-container .el-textarea.is-readonly .el-textarea__inner {
  background-color: #f5f7fa;
  color: #606266;
  cursor: not-allowed;
}

/* 按钮样式 */
.bind-button {
  /* margin-right: 20px; */
  min-width: 150px;
}

.reset-button {
  min-width: 120px;
}

/* 已扫码列表容器 */
.scanned-list-container {
  width: 100%;
  overflow-x: auto;
}

/* 记录卡片样式 */
.records-container {
  display: flex;
  flex-direction: column;
  gap: 15px;
  padding: 10px 0;
}

.record-card {
  cursor: pointer;
  transition: all 0.3s ease;
}

.record-card:hover {
  transform: translateY(-2px);
}

.record-card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: 10px;
}

.outer-box-barcode {
  font-weight: bold;
  font-size: 14px;
  word-break: break-all;
  white-space: normal;
  line-height: 1.4;
  flex: 1;
  min-width: 0;
}

.record-card-content {
  margin-top: 10px;
}

.record-info {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 10px;
  font-size: 14px;
}

.product-preview {
  margin-top: 10px;
}

.product-tag {
  /* margin-top: 10px; */
  margin-right: 10px;
}

.product-count-text {
  cursor: pointer;
  text-decoration: underline;
}

.product-count-text:hover {
  color: #409eff;
}

/* 展开动画 */
.expand-enter-active,
.expand-leave-active {
  transition: all 0.3s ease;
  max-height: 1000px;
  overflow: hidden;
}

.expand-enter-from,
.expand-leave-to {
  max-height: 0;
  overflow: hidden;
  opacity: 0;
}

/* 产品列表展开样式 */
.product-list-inline {
  margin-top: 10px;
  /* padding-top: 10px; */
}

.product-tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.product-tag-item {
  margin: 0;
}

.collapse-tag {
  cursor: pointer;
  background-color: #f0f9ff;
  border-color: #b3d8ff;
  color: #409eff;
}

.collapse-tag:hover {
  background-color: #409eff;
  color: white;
}

/* 加载更多 */
.loading-more {
  text-align: center;
  padding: 20px;
}

.no-data {
  padding: 40px 0;
}

/* 详情对话框 */
.detail-content {
  max-height: 60vh;
  overflow-y: auto;
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
  .app-container {
    padding: 5px;
  }
  
  .card-header {
    font-size: 14px;
  }
  
  .outer-box-input,
  .product-input {
    max-width: 100%;
  }
  
  .bind-button,
  .reset-button {
    width: 100%;
    margin-right: 0;
    margin-bottom: 10px;
  }
  
  .button-row .el-col {
    display: flex;
    flex-direction: column;
    align-items: center;
  }
  
  .record-info {
    flex-direction: column;
    gap: 5px;
  }
  
  .el-table {
    font-size: 12px;
  }
  
  .el-table th,
  .el-table td {
    padding: 8px;
  }
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
    /* padding: 12px 20px; */
    font-size: 16px;
  }
  
  .el-input {
    font-size: 16px;
  }
  
  .el-input__inner {
    padding: 12px;
  }
  
  .el-table {
    font-size: 11px;
  }
  
  .el-table th,
  .el-table td {
    padding: 6px;
  }
}
</style>
