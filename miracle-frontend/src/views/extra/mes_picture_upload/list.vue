<template>
  <!-- 新增：页面和弹窗使用独立样式类，PDA 适配只影响当前工单图片页面。 -->
  <div class="app-container order-image-page">
    <el-form class="order-search" :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="订单号" prop="productionOrderNo">
        <el-input
            v-model="queryParams.productionOrderNo"
            placeholder="请输入订单号"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="物料编码" prop="materialCode">
        <el-input
            v-model="queryParams.materialCode"
            placeholder="请输入物料编码"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="物料名称" prop="materialName">
        <el-input
            v-model="queryParams.materialName"
            placeholder="请输入物料名称"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8 order-toolbar">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleOrderImport" v-hasPermi="['mes_picture:add']">订单导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button 
          type="success" 
          plain 
          icon="Edit" 
          :disabled="single" 
          @click="handleEdit"
          v-hasPermi="['mes_picture:update']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button 
          type="danger" 
          plain 
          icon="Delete" 
          :disabled="multiple" 
          @click="handleDelete"
          v-hasPermi="['mes_picture:delete']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5" v-show="false">
        <el-button type="warning" plain icon="Download" @click="handleExport">导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 新增：窄屏沿用物料页的表格操作，点击行可选择，图片及按钮点击不重复切换选择。 -->
    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange"
      @row-click="handleRowClick" ref="tableRef" row-key="fullOrderNo">
      <el-table-column type="selection" width="40" align="center" />
      <el-table-column label="订单号" align="center" min-width="180" show-overflow-tooltip>
        <template #default="scope">
          {{ getDisplayOrderNo(scope.row.fullOrderNo) }}
        </template>
      </el-table-column>
      <el-table-column label="车间" align="center" prop="workshop" min-width="160" show-overflow-tooltip/>
      <el-table-column label="物料编码" align="center" prop="materialCode" min-width="140" show-overflow-tooltip/>
      <el-table-column label="物料名称" align="center" prop="materialName" min-width="180" show-overflow-tooltip/>
      <el-table-column label="计划数量" align="center" prop="plannedQuantity" min-width="120" show-overflow-tooltip/>
      <!-- 历史图片列宽：min-width="160"；现为四张 40px 缩略图预留单行宽度。 -->
      <el-table-column label="物料图片" align="center" min-width="210">
        <template #default="scope">
          <div class="table-image-list">
            <!-- 新增：列表只加载缩略图；历史 URL 只供点击后预览，缺图时显示查看入口。 -->
            <template v-if="scope.row.imageIds?.length">
              <!-- 历史循环：scope.row.imageIds.slice(0, 4)，数量上限现统一由 LIST_IMAGE_LIMIT 管理。 -->
              <BusinessImage v-for="id in getVisibleImageIds(scope.row)" :key="id" :image-id="id"
                :thumbnail="thumbnailUrls[id]" @error="error => proxy.$modal.msgError(error.message)" />
              <!-- 历史数量提示：<span v-if="scope.row.imageIds.length > 4">+{{ scope.row.imageIds.length - 4 }}</span> -->
            </template>
            <template v-else-if="getImageList(scope.row).length">
              <!-- 新增：历史 URL 同样只展示四项，其余图片保留在完整编辑数据中。 -->
              <BusinessImage v-for="(url, idx) in getImageList(scope.row).slice(0, LIST_IMAGE_LIMIT)" :key="idx" :fallback="url" />
            </template>
            <!-- 历史列表原图实现保留：
            <template v-if="getImageList(scope.row).length > 0">
              <el-image
                  v-for="(img, idx) in getImageList(scope.row).slice(0, 4)"
                  :key="idx"
                  :src="img"
                  fit="cover"
                  style="width: 40px; height: 40px; border-radius: 4px; margin-right: 4px;"
                  :preview-src-list="getImageList(scope.row)"
                  preview-teleported="true"
              />
              <span v-if="getImageList(scope.row).length > 4" style="color: #909399; font-size: 12px;">
                +{{ getImageList(scope.row).length - 4 }}
              </span>
            </template>
            -->
            <span v-else style="color: #909399;">无</span>
          </div>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" min-width="180" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ formatDate(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="createBy" min-width="120" show-overflow-tooltip/>
    </el-table>

    <pagination
        v-show="total>0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        :layout="isPda ? 'prev, pager, next' : 'total, sizes, prev, pager, next, jumper'"
        :pager-count="isPda ? 5 : 7"
        @pagination="getList"
    />

    <!-- ==================== 修改对话框 ==================== -->
    <!-- 历史固定宽度：width="700px"；新增：参考物料页，窄屏弹窗随可用宽度收缩。 -->
    <el-dialog 
      :title="title" 
      v-model="open" 
      width="min(700px, calc(100vw - 24px))"
      class="order-image-dialog"
      append-to-body
      @close="handleDialogClose"
      @opened="focusEditOrderInput"
      :close-on-click-modal="false"
      :close-on-press-escape="!isSubmitting && editUploading === 0"
      :show-close="!isSubmitting && editUploading === 0"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" :disabled="isSubmitting">
        <el-form-item label="订单号" prop="productionOrderNo">
          <!-- 历史输入：<el-input v-model="form.productionOrderNo" readonly /> -->
          <!-- 新增：修改弹窗打开时定位订单号，仍只读，不允许改动已有工单标识。 -->
          <el-input ref="editOrderInputRef" v-model="form.productionOrderNo" readonly inputmode="none" />
        </el-form-item>
        <el-form-item label="物料编码" prop="materialCode">
          <el-input v-model="form.materialCode" readonly />
        </el-form-item>
        <el-form-item label="物料名称" prop="materialName">
          <el-input v-model="form.materialName" readonly />
        </el-form-item>
<!--        <el-form-item label="车间" prop="workshop">-->
<!--          <el-input v-model="form.workshop" placeholder="请输入车间" />-->
<!--        </el-form-item>-->
<!--        <el-form-item label="计划数量" prop="plannedQuantity">-->
<!--          <el-input-number-->
<!--              v-model="form.plannedQuantity"-->
<!--              :precision="3"-->
<!--              :step="1"-->
<!--              controls-position="right"-->
<!--              style="width: 100%"-->
<!--          />-->
<!--        </el-form-item>-->
        <el-form-item label="物料图片">
          <div class="image-upload-container">
            <div class="image-list">
              <div v-for="(img, index) in form.images" :key="index" class="image-item">
                <img :src="img" class="avatar" />
                <div class="image-actions">
                  <el-button type="danger" icon="Delete" size="small" circle @click="handleRemoveImage(index, 'form')" />
                </div>
              </div>
              <el-upload
                  v-if="form.images.length + retainedEditImages.length < 15"
                  class="avatar-uploader"
                  action=""
                  :show-file-list="false"
                  :on-change="handleImageUpload"
                  :auto-upload="false"
                  accept="image/jpeg,image/jpg,image/png"
                  :multiple="false"
              >
                <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </div>
          </div>
          <div class="upload-tip">支持 JPG/PNG 格式，最多上传 15 张</div>
        </el-form-item>
        <!-- 新增：修改弹窗不展示备注，保留字段及保存逻辑，避免清空已有备注。历史表单如下：
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
        -->
      </el-form>
      <template #footer>
        <div class="dialog-footer order-import-footer">
          <!-- 历史操作保留：<el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button> -->
          <!-- 新增：两个弹窗统一使用居中的导入入口，提交期间防止重复点击。 -->
          <el-button class="order-import-button" type="primary" :loading="isSubmitting" :disabled="editUploading > 0" @click="submitForm">确定</el-button>
          <el-button class="order-import-button" @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- ==================== 订单导入对话框 ==================== -->
    <!-- 历史导入弹窗同样固定为 700px；新增：共享移动端表单和底部操作区。 -->
    <el-dialog title="订单导入" v-model="orderDialogVisible" width="min(700px, calc(100vw - 24px))"
      class="order-image-dialog" append-to-body @close="resetOrderForm"
      @opened="focusImportOrderInput"
      :close-on-click-modal="false" :close-on-press-escape="!isImporting && orderUploading === 0"
      :show-close="!isImporting && orderUploading === 0">
      <el-form ref="orderFormRef" :model="orderForm" :rules="orderRules" label-width="120px" :disabled="isImporting">
        <el-form-item label="订单号" prop="originalOrderNo">
          <!-- 历史模式：inputmode="none"。新增：恢复普通文本输入以兼容 PDA 默认文本框优化，不要求用户修改全局扫码设置。 -->
          <el-input
              ref="importOrderInputRef"
              v-model="orderForm.originalOrderNo"
              inputmode="text"
              placeholder="请输入订单号"
              @focus="handleOrderScanFocus"
              @input.capture="handleOrderScanInput"
              @input="handleOrderNoInput"
              @blur="handleOrderNoBlur"
              @keyup.enter="handleOrderNoEnter"
              clearable
          />
        </el-form-item>
        <!-- 实际订单号已隐藏 -->
        <el-form-item label="物料编码">
          <el-input v-model="orderForm.materialCode" readonly />
        </el-form-item>
        <el-form-item label="物料名称">
          <el-input v-model="orderForm.materialName" readonly />
        </el-form-item>
<!--        <el-form-item label="车间">-->
<!--          <el-input v-model="orderForm.materialWorkshop" readonly />-->
<!--        </el-form-item>-->
<!--        <el-form-item label="计划数量">-->
<!--          <el-input-number-->
<!--              v-model="orderForm.plannedQuantity"-->
<!--              :precision="3"-->
<!--              :step="1"-->
<!--              controls-position="right"-->
<!--              style="width: 100%"-->
<!--              disabled-->
<!--          />-->
<!--        </el-form-item>-->
        <el-form-item label="物料图片">
          <div class="image-upload-container">
            <div class="image-list">
              <div v-for="(img, index) in orderForm.materialImages" :key="index" class="image-item">
                <img :src="img" class="avatar" />
                <div class="image-actions">
                  <el-button type="danger" icon="Delete" size="small" circle @click="handleRemoveImage(index, 'order')" />
                </div>
              </div>
              <el-upload
                  v-if="orderForm.materialImages.length < 15"
                  class="avatar-uploader"
                  action=""
                  :show-file-list="false"
                  :on-change="handleOrderImageUpload"
                  :auto-upload="false"
                  accept="image/jpeg,image/jpg,image/png"
                  :multiple="false"
              >
                <el-icon class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </div>
          </div>
          <div class="upload-tip">支持 JPG/PNG 格式，最多上传 15 张</div>
        </el-form-item>
        <!-- 新增：订单导入弹窗同样不展示备注；以下已注释的历史代码保留。 -->
<!--        <el-form-item label="备注">-->
<!--          <el-input v-model="orderForm.remark" placeholder="请输入备注" type="textarea" :rows="3" />-->
<!--        </el-form-item>-->
      </el-form>
      <template #footer>
        <div class="dialog-footer order-import-footer">
          <!-- 历史操作保留：<el-button type="primary" @click="submitOrderForm">确 定</el-button>
          <el-button @click="orderDialogVisible = false">取 消</el-button> -->
          <el-button class="order-import-button" type="primary" :loading="isImporting" :disabled="orderUploading > 0" @click="submitOrderForm">导入</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="ProdOrderList">
import { Plus } from '@element-plus/icons-vue'
// 历史导入：import { ref, reactive, toRefs, onMounted, getCurrentInstance, nextTick } from 'vue'
import { ref, reactive, toRefs, onMounted, onBeforeUnmount, getCurrentInstance, nextTick } from 'vue'
// 新增：断点随屏幕旋转更新，离开页面自动释放监听，PDA 使用紧凑分页。
import { useMediaQuery } from '@vueuse/core'
import {
  listProdOrder,
  delProdOrder,
  delBatchProdOrder,
  updateProdOrder
} from "@/api/extra/mes_picture_upload.js"
import { getProdOrder, addProdOrder } from "@/api/extra/mes_picture_upload.js"
// 历史导入保留：import { uploadImage, deleteImage }
import { uploadImage, getImagePreviewUrl } from "@/api/extra/image_store.js"

// 新增：列表批量缩略图与点击原图组件。
import BusinessImage from '@/components/BusinessImage/index.vue'
import { useBusinessThumbnails } from '@/composables/useBusinessThumbnails'
const { thumbnailUrls, refreshThumbnails } = useBusinessThumbnails()
// 新增：只裁剪列表显示和缩略图查询，不裁剪编辑、保存时的完整图片集合。
const LIST_IMAGE_LIMIT = 4
const isPda = useMediaQuery('(max-width: 768px)')
function getVisibleImageIds(row) {
  return (row.imageIds || []).slice(0, LIST_IMAGE_LIMIT)
}
console.log('ProdOrderList module loaded');

const { proxy } = getCurrentInstance();

// ==================== 响应式数据 ====================
const dataList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const formRef = ref(null);
// 新增：弹窗动画完成后聚焦原生输入框；可编辑订单号保持普通输入通道，兼容设备默认扫码输出。
const editOrderInputRef = ref(null);
const importOrderInputRef = ref(null);
// 新增：整段条码注入采用短暂停顿兜底，逐字符输入不会因普通停顿而提前失焦。
const ORDER_SCAN_IDLE_MS = 800;
let orderScanTimer = null;
let orderScanBatch = false;
let orderScanLastValue = '';
let skipOrderScanBlur = false;

/** 新增：关闭、清空或完成扫码后取消延迟任务，避免在其他输入框操作时被抢焦点。 */
function resetOrderScanState() {
  if (orderScanTimer !== null) clearTimeout(orderScanTimer);
  orderScanTimer = null;
  orderScanBatch = false;
  orderScanLastValue = '';
}

/** 新增：重新点击已有订单号时记录当前值，避免把单字符手动修改误判为整段扫码。 */
function handleOrderScanFocus(event) {
  resetOrderScanState();
  orderScanLastValue = String(event.target?.value || '');
}

/** 新增：观察原生输入事件，兼容文本框优化整段注入；不拦截或改写扫码字符。 */
function handleOrderScanInput(event) {
  if (orderScanTimer !== null) clearTimeout(orderScanTimer);
  orderScanTimer = null;
  const value = String(event.target?.value || '');
  const previous = orderScanLastValue;
  orderScanLastValue = value;
  if (event.isComposing || event.inputType?.includes('Composition') || !value.trim()) {
    orderScanBatch = false;
    return;
  }
  // 单字符键盘输出依靠结束回车；整段注入或粘贴可在无回车时自动完成。
  orderScanBatch = orderScanBatch || event.inputType === 'insertFromPaste' ||
    (typeof event.data === 'string' && event.data.length > 1) || (!previous && value.length > 1);
  if (!orderScanBatch) return;
  const input = event.target;
  orderScanTimer = setTimeout(() => {
    orderScanTimer = null;
    if (orderDialogVisible.value && document.activeElement === input &&
        orderForm.originalOrderNo === value && !isImporting.value && orderUploading.value === 0)
      handleOrderNoEnter();
  }, ORDER_SCAN_IDLE_MS);
}

// 新增：页面销毁时释放扫码结束检测任务。
onBeforeUnmount(resetOrderScanState);

function focusEditOrderInput() {
  if (open.value) editOrderInputRef.value?.input?.focus({ preventScroll: true });
}

function focusImportOrderInput() {
  orderScanLastValue = orderForm.originalOrderNo || '';
  if (orderDialogVisible.value) importOrderInputRef.value?.input?.focus({ preventScroll: true });
}
const queryRef = ref(null);
const tableRef = ref(null);
const originImageIds = ref([]);
const isSubmitting = ref(false);
// 新增：新订单导入也使用独立提交锁，连续导入时避免重复请求。
const isImporting = ref(false);
// 新增：清空图片区域后保留本次编辑会话已保存的关联，下批提交追加而非替换。
const retainedEditImageIds = ref([]);
const retainedEditImages = ref([]);
// 新增：同一会话已导入的内部工单号参与去重，列表刷新未完成时也不会重复导入上一编号。
const importedOrderNos = ref([]);
// 新增：上传期间禁用导入；代次检查避免旧预览响应覆盖新上传或已清空的图片区域。
const editUploading = ref(0);
const orderUploading = ref(0);
const editImageVersion = ref(0);
const orderImageVersion = ref(0);
const orderNoExists = ref(false);

// ==================== 订单导入对话框数据 ====================
const orderDialogVisible = ref(false);
const orderFormRef = ref(null);
const orderOriginImageIds = ref([]);
const orderForm = reactive({
  originalOrderNo: '',
  productionOrderNo: '',
  materialCode: '',
  materialName: '',
  materialWorkshop: '',
  plannedQuantity: null,
  materialImages: [],
  materialImageIds: [],
  remark: ''
});

// 订单导入校验规则
const orderRules = {
  originalOrderNo: [
    { required: true, message: '请输入订单号', trigger: 'blur' }
  ]
};

// 表单数据
const data = reactive({
  form: {
    productionOrderNo: '',
    workshop: '',
    materialCode: '',
    materialName: '',
    plannedQuantity: null,
    images: [],
    imageIds: [],
    remark: ''
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    productionOrderNo: '',
    materialCode: '',
    materialName: ''
  },
  rules: {
    workshop: [
      { required: true, message: '请输入车间', trigger: 'blur' }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);

// ==================== 订单号显示处理 ====================

/**
 * 获取显示用的订单号（去掉后缀）
 * 例如：WO20260713-001 -> WO20260713
 */
function getDisplayOrderNo(fullOrderNo) {
  if (!fullOrderNo) return '';
  const match = fullOrderNo.match(/^(.*?)(-\d{3})?$/)
  return match ? match[1] : fullOrderNo
}

/**
 * 检查订单号是否在列表中已存在（使用完整订单号）
 */
function checkOrderNoInList(orderNo) {
  if (!orderNo) return false;
  // 历史检查：return dataList.value.some(item => item.fullOrderNo === orderNo);
  return importedOrderNos.value.includes(orderNo) || dataList.value.some(item => item.fullOrderNo === orderNo);
}

// ==================== 生成带后缀的订单号 ====================
function generateOrderSuffix(baseOrderNo) {
  if (!baseOrderNo || !baseOrderNo.trim()) return '';
  
  const trimmed = baseOrderNo.trim();
  
  // 历史来源：dataList.value.map(item => item.fullOrderNo)；新增：合并当前会话刚导入的编号。
  const existingOrders = [...new Set([...dataList.value.map(item => item.fullOrderNo), ...importedOrderNos.value])]
    .filter(no => no && no.startsWith(trimmed + '-'));
  
  const maxSuffix = existingOrders.reduce((max, no) => {
    const parts = no.split('-');
    if (parts.length >= 2) {
      const suffix = parseInt(parts[parts.length - 1]);
      if (!isNaN(suffix) && suffix > max) {
        return suffix;
      }
    }
    return max;
  }, 0);
  
  const exactMatch = dataList.value.some(item => item.fullOrderNo === trimmed);
  
  let nextNumber = maxSuffix + 1;
  if (exactMatch && maxSuffix === 0) {
    nextNumber = 1;
  }
  if (maxSuffix === 0 && !exactMatch) {
    nextNumber = 1;
  }
  
  return `${trimmed}-${String(nextNumber).padStart(3, '0')}`;
}

// ==================== 从行数据中提取图片列表 ====================
function getImageList(row) {
  if (!row) return [];
  
  if (row.images && Array.isArray(row.images) && row.images.length > 0) {
    return row.images;
  }
  
  if (row.image && typeof row.image === 'string' && row.image.length > 0) {
    return row.image.split(',').filter(url => url.trim().length > 0);
  }
  
  if (row.imageUrl && typeof row.imageUrl === 'string' && row.imageUrl.length > 0) {
    return [row.imageUrl];
  }
  
  return [];
}

// ==================== 查询列表 ====================
function getList() {
  loading.value = true;
  listProdOrder(queryParams.value).then(response => {
    const list = response.rows || response.data || [];
    console.log('接口返回原始数据:', list);
    
    const uniqueMap = new Map();
    list.forEach(item => {
      const key = item.productionOrderNo;
      if (!uniqueMap.has(key)) {
        uniqueMap.set(key, item);
      }
    });
    const uniqueList = Array.from(uniqueMap.values());
    
    dataList.value = uniqueList.map(item => {
      const images = getImageList(item);
      let imageIds = [];
      if (item.imageIds) {
        if (typeof item.imageIds === 'string' && item.imageIds.length > 0) {
          imageIds = item.imageIds.split(',').filter(id => id.trim().length > 0);
        } else if (Array.isArray(item.imageIds)) {
          imageIds = item.imageIds;
        }
      }
      
      return {
        ...item,
        fullOrderNo: item.productionOrderNo,
        images: images,
        imageIds: imageIds
      };
    });
    
    total.value = response.total || dataList.value.length;
    // 新增：缩略图失败单独提示，列表数据仍保留；无 ID 历史图不请求此接口。
    // 历史查询：refreshThumbnails(dataList.value.flatMap(row => row.imageIds || []))。
    // 新增：隐藏的第五张及以后不请求缩略图地址，完整 ID 仍留在行数据中供编辑保存。
    refreshThumbnails(dataList.value.flatMap(getVisibleImageIds)).catch(error => proxy.$modal.msgWarning(error.message));
    loading.value = false;
  }).catch(error => {
    loading.value = false;
    console.error('获取数据列表失败:', error);
    proxy.$modal.msgError('获取数据失败');
  });
}

// 取消按钮
function cancel() {
  const hasChanges = checkFormChanges();
  if (hasChanges) {
    proxy.$modal.confirm('有未保存的修改，确定要取消吗？').then(() => {
      open.value = false;
      reset();
    }).catch(() => {});
  } else {
    open.value = false;
    reset();
  }
}

// 表单重置
function reset() {
  // 新增：只有主动关闭或切换记录才清除追加基线，并使旧预览请求失效。
  retainedEditImageIds.value = [];
  retainedEditImages.value = [];
  editImageVersion.value++;
  form.value = {
    productionOrderNo: '',
    workshop: '',
    materialCode: '',
    materialName: '',
    plannedQuantity: null,
    images: [],
    imageIds: [],
    remark: ''
  };
  originImageIds.value = [];
  if (formRef.value) {
    formRef.value.resetFields();
  }
}

// 重置订单导入表单
function resetOrderForm() {
  resetOrderScanState();
  skipOrderScanBlur = false;
  // 新增：关闭订单导入窗口时终止旧图片预览结果的回填。
  orderImageVersion.value++;
  orderForm.originalOrderNo = '';
  orderForm.productionOrderNo = '';
  orderForm.materialCode = '';
  orderForm.materialName = '';
  orderForm.materialWorkshop = '';
  orderForm.plannedQuantity = null;
  orderForm.materialImages = [];
  orderForm.materialImageIds = [];
  orderForm.remark = '';
  orderOriginImageIds.value = [];
  orderNoExists.value = false;
  if (orderFormRef.value) {
    orderFormRef.value.resetFields();
  }
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  if (queryRef.value) {
    queryRef.value.resetFields();
  }
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.fullOrderNo);
  single.value = selection.length !== 1;
  multiple.value = selection.length === 0;
}

/** 新增：PDA 可点击行选择记录；复选框、图片预览和其他交互元素仍执行自身操作。 */
function handleRowClick(row, column, event) {
  if (event?.target?.closest('button, img, input, a, .el-checkbox, .el-image-viewer__wrapper')) return
  tableRef.value?.toggleRowSelection(row)
}

// ==================== 修改 ====================
function handleEdit() {
  if (ids.value.length === 0) {
    proxy.$modal.msgWarning('请选择一条记录进行修改');
    return;
  }
  if (ids.value.length > 1) {
    proxy.$modal.msgWarning('只能选择一条记录进行修改');
    return;
  }

  const row = dataList.value.find(item => item.fullOrderNo === ids.value[0]);
  if (row) {
    // 新增：重新打开一条工单时，以其完整图片作为可编辑集合，追加基线从空开始。
    retainedEditImageIds.value = [];
    retainedEditImages.value = [];
    const imageVersion = ++editImageVersion.value;
    const imageIds = row.imageIds || [];
    form.value = {
      productionOrderNo: row.fullOrderNo || '',
      workshop: row.workshop || '',
      materialCode: row.materialCode || '',
      materialName: row.materialName || '',
      plannedQuantity: row.plannedQuantity || null,
      images: [...(row.images || [])],
      imageIds: [...imageIds],
      remark: row.remark || ''
    };
    originImageIds.value = [...imageIds];
    // 新增：私有桶图片在编辑打开后才逐个获取原图签名，列表不下载原图。
    if (imageIds.length) Promise.all(imageIds.map(id => getImagePreviewUrl(id))).then(results => {
      // 历史条件只校验窗口及订单号；新增：用户已经上传/移除图片或导入后，不覆盖当前集合。
      if (open.value && form.value.productionOrderNo === row.fullOrderNo && imageVersion === editImageVersion.value)
        form.value.images = results.map(result => result.data || '');
    }).catch(error => proxy.$modal.msgWarning(error.message));
    open.value = true;
    title.value = "修改订单";
    isSubmitting.value = false;
  } else {
    proxy.$modal.msgError('未找到该记录');
  }
}

// ==================== 检查表单是否有变化 ====================
function checkFormChanges() {
  if (!open.value) return false;
  
  const original = dataList.value.find(item => 
    item.fullOrderNo === form.value.productionOrderNo
  );
  
  if (!original) return true;
  
  if (original.workshop !== form.value.workshop) return true;
  if (original.plannedQuantity !== form.value.plannedQuantity) return true;
  if ((original.remark || '') !== (form.value.remark || '')) return true;
  
  const originalImages = original.images || [];
  // 历史集合：const currentImages = form.value.images || [];
  const currentImages = [...retainedEditImages.value, ...(form.value.images || [])];
  // 新增：有持久化 ID 时只比较 ID，短期签名变化不代表用户修改了图片。
  const oldIds = original.imageIds || [];
  // 历史集合：const newIds = form.value.imageIds || [];
  const newIds = [...new Set([...retainedEditImageIds.value, ...(form.value.imageIds || [])])];
  if (oldIds.length || newIds.length) return JSON.stringify(oldIds) !== JSON.stringify(newIds);
  
  if (originalImages.length !== currentImages.length) return true;
  
  for (let i = 0; i < originalImages.length; i++) {
    if (originalImages[i] !== currentImages[i]) return true;
  }
  
  return false;
}

// ==================== 图片上传 ====================

/** 上传前校验 */
function handleBeforeUpload(file) {
  const fileName = file.name || '';
  const ext = fileName.split('.').pop().toLowerCase();
  const validExts = ['jpg', 'jpeg', 'png'];
  const validTypes = ['image/jpeg', 'image/jpg', 'image/png'];
  
  // 新增：JPEG/PNG 类型与 10MB 大小在手动上传路径显式检查。
  if (file.size > 10 * 1024 * 1024) { proxy.$modal.msgError('图片不能超过 10 MB'); return false }
  const isValid = validExts.includes(ext) && validTypes.includes(file.type);
  
  if (!isValid) {
    proxy.$modal.msgError('图片格式错误，请上传 JPG 或 PNG 格式的图片');
    return false;
  }
  return true;
}

/** 图片实际上传 */
/** 新增：上传成功前禁止导入，预览和请求返回只作用于原来的表单会话。 */
function uploadImageToForm(file, targetType) {
  const source = file.raw || file
  if (!source || !handleBeforeUpload(source)) return
  const editing = targetType === 'form'
  if (!editing && targetType !== 'order') return
  if (editing ? isSubmitting.value : isImporting.value) return
  const pending = editing ? editUploading : orderUploading
  const count = editing ? retainedEditImages.value.length + form.value.images.length : orderForm.materialImages.length
  if (count + pending.value >= 15) { proxy.$modal.msgWarning('最多只能上传 15 张图片'); return }
  const editingForm = form.value
  const originalNo = orderForm.originalOrderNo
  const generatedNo = orderForm.productionOrderNo
  if (editing) editImageVersion.value++
  else orderImageVersion.value++
  pending.value++
  proxy.$modal.loading('正在上传图片，请稍候...')
  uploadImage(source, 'PROD_ORDER').then(response => {
    if (response.code !== 200 || !response.data?.imageId || !response.data?.imageUrl)
      throw new Error(response.msg || '服务端未返回图片信息')
    if (editing) {
      // 窗口关闭或切换记录后的上传仍是暂存图片，不写入另一个表单。
      if (!open.value || form.value !== editingForm) return
      form.value.images.push(response.data.imageUrl)
      form.value.imageIds.push(response.data.imageId)
    } else {
      if (!orderDialogVisible.value || originalNo !== orderForm.originalOrderNo || generatedNo !== orderForm.productionOrderNo) return
      orderForm.materialImages.push(response.data.imageUrl)
      orderForm.materialImageIds.push(response.data.imageId)
    }
    proxy.$modal.msgSuccess('图片上传成功')
  }).catch(error => proxy.$modal.msgError('图片上传失败：' + error.message))
    .finally(() => {
      pending.value--
      if (editUploading.value + orderUploading.value === 0) proxy.$modal.closeLoading()
    })
}

// 历史实现完整保留：连续导入改造前的 uploadImageToForm。
// function uploadImageToForm(file, targetType) {
//   const source = file.raw || file
//   if (!source || !handleBeforeUpload(source)) return
//   proxy.$modal.loading('正在上传图片，请稍候...')
//   uploadImage(source, 'PROD_ORDER').then(response => {
//     if (response.code !== 200 || !response.data?.imageId || !response.data?.imageUrl) {
//       throw new Error(response.msg || '服务端未返回图片信息')
//     }
//     if (targetType === 'form') {
//       form.value.images.push(response.data.imageUrl)
//       form.value.imageIds.push(response.data.imageId)
//     } else if (targetType === 'order') {
//       orderForm.materialImages.push(response.data.imageUrl)
//       orderForm.materialImageIds.push(response.data.imageId)
//     } else {
//       throw new Error('未知图片用途')
//     }
//     proxy.$modal.msgSuccess('图片上传成功')
//   }).catch(error => {
//     proxy.$modal.msgError('图片上传失败：' + error.message)
//   }).finally(() => proxy.$modal.closeLoading())
// }

/** 修改对话框 - 图片上传 */
function handleImageUpload(file) {
  // 历史数量只统计可见图片；新增：本会话已保存图片也计入工单最多 15 张限制。
  if (form.value.images.length + retainedEditImages.value.length >= 15) {
    proxy.$modal.msgWarning('最多只能上传 15 张图片');
    return;
  }
  uploadImageToForm(file, 'form');
}

/** 订单导入 - 图片上传 */
function handleOrderImageUpload(file) {
  if (orderForm.materialImages.length >= 15) {
    proxy.$modal.msgWarning('最多只能上传 15 张图片');
    return;
  }
  uploadImageToForm(file, 'order');
}

// ==================== 删除图片 ====================
function handleRemoveImage(index, targetType) {
  if (targetType === 'form') {
    editImageVersion.value++;
    form.value.images.splice(index, 1)
    form.value.imageIds.splice(index, 1)
  } else if (targetType === 'order') {
    orderImageVersion.value++;
    orderForm.materialImages.splice(index, 1)
    orderForm.materialImageIds.splice(index, 1)
  }
}

// ==================== 表单提交 ====================
function submitForm() {
  // 新增：等待本弹窗图片上传完成，避免导入后又写入刚到达的上传结果。
  if (isSubmitting.value || editUploading.value > 0) return
  if (!formRef.value) {
    proxy.$modal.msgWarning('表单未初始化')
    return
  }
  formRef.value.validate(valid => {
    // 新增：异步校验期间再次点击也只能产生一个导入请求。
    if (isSubmitting.value) return
    if (!valid) {
      proxy.$modal.msgWarning('请完善表单信息')
      return
    }
    if (!checkFormChanges()) {
      proxy.$modal.msgWarning('数据未发生改变')
      // 历史连续修改行为：无变更时也保留窗口和已输入字段。
      // 新增：修改弹窗点击确定后无变更也关闭；校验失败或保存失败仍保留窗口。
      open.value = false
      return
    }
    isSubmitting.value = true
    doSubmitUpdate()
  })
}

/** 执行提交更新 */
/** 历史连续修改行为：只清图片输入区，保留弹窗供下一批追加。 */
/** 新增：修改保存成功后关闭弹窗；只有新增导入继续保留弹窗。 */
async function doSubmitUpdate() {
  const currentIds = [...new Set([...retainedEditImageIds.value, ...form.value.imageIds])]
  const currentImages = [...retainedEditImages.value, ...form.value.images]
  const submitData = {
    productionOrderNo: form.value.productionOrderNo,
    workshop: form.value.workshop,
    materialCode: form.value.materialCode,
    materialName: form.value.materialName,
    plannedQuantity: form.value.plannedQuantity,
    image: currentImages.join(','),
    imageIds: currentIds,
    remark: form.value.remark
  }
  proxy.$modal.loading('正在导入...')
  try {
    await updateProdOrder(submitData)
    retainedEditImageIds.value = [...currentIds]
    retainedEditImages.value = [...currentImages]
    originImageIds.value = [...currentIds]
    // 立即更新变更比较基线，避免列表刷新尚未完成时再次空提交或误删已保存图片。
    const row = dataList.value.find(item => item.fullOrderNo === submitData.productionOrderNo)
    if (row) Object.assign(row, submitData, { images: [...currentImages], imageIds: [...currentIds] })
    editImageVersion.value++
    form.value.images = []
    form.value.imageIds = []
    formRef.value?.clearValidate()
    proxy.$modal.msgSuccess('修改成功')
    // 新增：仅在后端更新成功后关闭修改窗口，保留失败时的输入供重试。
    open.value = false
    getList()
  } catch (error) {
    // 失败时保留输入和上传图片，允许用户直接重试。
    proxy.$modal.msgError('导入失败：' + (error.message || ''))
  } finally {
    proxy.$modal.closeLoading()
    isSubmitting.value = false
  }
}

// 历史实现完整保留：连续导入改造前的 doSubmitUpdate。
// async function doSubmitUpdate() {
//   const originalIds = [...originImageIds.value]
//   const currentIds = [...form.value.imageIds]
//   const submitData = {
//     productionOrderNo: form.value.productionOrderNo,
//     workshop: form.value.workshop,
//     materialCode: form.value.materialCode,
//     materialName: form.value.materialName,
//     plannedQuantity: form.value.plannedQuantity,
//     image: form.value.images.join(','),
//     // 历史实现：imageIds: currentIds.join(','),
//     imageIds: currentIds,
//     remark: form.value.remark
//   }
//   proxy.$modal.loading('正在保存修改...')
//   try {
//     await updateProdOrder(submitData)
//     // 历史实现：await deleteRemovedImagesAfterSave(originalIds, currentIds)
//     // 新增：业务后端事务写 outbox，提交成功后由后台清理原图和缩略图。
//     originImageIds.value = currentIds
//     proxy.$modal.msgSuccess('修改成功')
//     open.value = false
//     reset()
//     getList()
//   } catch (error) {
//     proxy.$modal.msgError('修改失败：' + (error.message || ''))
//   } finally {
//     proxy.$modal.closeLoading()
//     isSubmitting.value = false
//   }
// }

/** 对话框关闭回调 */
function handleDialogClose() {
  if (open.value === false) {
    const hasChanges = checkFormChanges();
    if (hasChanges) {
      proxy.$modal.confirm('有未保存的修改，确定要关闭吗？').then(() => {
        reset();
        isSubmitting.value = false;
      }).catch(() => {
        open.value = true;
      });
    } else {
      reset();
      isSubmitting.value = false;
    }
  }
}

// ==================== 删除按钮操作 ====================
function handleDelete() {
  if (ids.value.length === 0) {
    proxy.$modal.msgWarning('请选择至少一条记录进行删除')
    return
  }
  const records = dataList.value.filter(item => ids.value.includes(item.fullOrderNo))
  const imageIds = [...new Set(records.flatMap(item => Array.isArray(item.imageIds) ? item.imageIds : []))]
  const orderIds = [...ids.value]
  proxy.$modal.confirm('是否确认删除选中的订单数据？').then(async () => {
    try {
      if (orderIds.length === 1) await delProdOrder(orderIds[0])
      else await delBatchProdOrder(orderIds)
      // 历史实现：await deleteRemovedImagesAfterSave(imageIds, [])
      // 新增：批量删除也完全由服务端业务事务和 outbox 负责。
      getList()
      proxy.$modal.msgSuccess('删除成功')
    } catch (error) {
      proxy.$modal.msgError('删除失败：' + (error.message || ''))
    }
  }).catch(() => {})
}

/** 导出 */
function handleExport() {
  if (dataList.value.length === 0) {
    proxy.$modal.msgWarning('暂无数据可导出');
    return;
  }
  const headers = ['订单号', '车间', '物料编码', '物料名称', '计划数量', '图片数量','创建人', '创建时间'];
  const rows = dataList.value.map(item => [
    getDisplayOrderNo(item.fullOrderNo) || '',
    item.workshop || '',
    item.materialCode || '',
    item.materialName || '',
    item.plannedQuantity || '',
    (item.images || []).length || 0,
    item.createTime || ''
  ]);
  let csv = headers.join(',') + '\n' + rows.map(r => r.join(',')).join('\n');
  const blob = new Blob(['\uFEFF' + csv], { type: 'text/csv;charset=utf-8;' });
  const link = document.createElement('a');
  link.href = URL.createObjectURL(blob);
  link.download = `订单数据_${new Date().getTime()}.csv`;
  link.click();
  URL.revokeObjectURL(link.href);
}

// ==================== 订单导入 ====================

function handleOrderImport() {
  resetOrderForm();
  orderDialogVisible.value = true;
  nextTick(() => {
    if (orderFormRef.value) {
      orderFormRef.value.clearValidate();
    }
  });
}

/** 订单号输入时清空物料信息并生成带后缀的订单号 */
function handleOrderNoInput(value) {
  // 新增：切换订单号使此前的图片预览回填失效。
  orderImageVersion.value++;
  orderForm.materialCode = '';
  orderForm.materialName = '';
  orderForm.materialWorkshop = '';
  orderForm.plannedQuantity = null;
  orderForm.materialImages = [];
  orderForm.materialImageIds = [];
  orderOriginImageIds.value = [];
  
  if (value && value.trim()) {
    const generated = generateOrderSuffix(value.trim());
    orderForm.productionOrderNo = generated;
    orderNoExists.value = checkOrderNoInList(generated);
    if (orderNoExists.value) {
      const newGenerated = generateOrderSuffix(value.trim());
      orderForm.productionOrderNo = newGenerated;
      orderNoExists.value = checkOrderNoInList(newGenerated);
    }
  } else {
    orderForm.productionOrderNo = '';
    orderNoExists.value = false;
  }
}

/** 订单号回车查询 */
function handleOrderNoEnter(event) {
  // 新增：回车标志扫码完成；中文组合输入或正在导入时不执行结束处理。
  if (event?.isComposing || isImporting.value || orderUploading.value > 0) return;
  const originalNo = orderForm.originalOrderNo?.trim();
  if (!originalNo) return;

  resetOrderScanState();
  
  const generated = generateOrderSuffix(originalNo);
  orderForm.productionOrderNo = generated;
  orderNoExists.value = checkOrderNoInList(generated);
  queryOrderInfo(originalNo);
  // 新增：完整条码已经进入表单后再失焦，提示系统收起软键盘，不再立即重新聚焦。
  const input = importOrderInputRef.value?.input;
  if (input && document.activeElement === input) {
    skipOrderScanBlur = true;
    input.blur();
    // 原生 blur 通常同步触发；下一轮清理标记，避免某些设备未派发 blur 时影响后续操作。
    nextTick(() => { skipOrderScanBlur = false; });
  }
}

/** 订单号失焦处理 */
function handleOrderNoBlur() {
  // 新增：扫码主动失焦已查询过一次，不重复发送同一工单查询。
  resetOrderScanState();
  if (skipOrderScanBlur) { skipOrderScanBlur = false; return; }
  const originalNo = orderForm.originalOrderNo?.trim();
  if (!originalNo) {
    orderForm.productionOrderNo = '';
    orderNoExists.value = false;
    return;
  }
  
  const generated = generateOrderSuffix(originalNo);
  orderForm.productionOrderNo = generated;
  orderNoExists.value = checkOrderNoInList(generated);
  queryOrderInfo(originalNo);
}

/** 查询订单信息 */
function queryOrderInfo(originalNo) {
  if (!originalNo) return;

  // 新增：查询仍可填充物料信息，但旧图片结果不能覆盖查询期间上传或导入后的图片区域。
  const imageVersion = ++orderImageVersion.value;

  getProdOrder(originalNo).then(response => {
    if (!orderDialogVisible.value || orderForm.originalOrderNo.trim() !== originalNo) return;
    const data = response.data || response;
    if (data && data.productionOrderNo) {
      const generated = generateOrderSuffix(originalNo);
      orderForm.productionOrderNo = generated;
      orderNoExists.value = checkOrderNoInList(generated);
      
      orderForm.materialCode = data.materialCode || '';
      orderForm.materialName = data.materialName || '';
      orderForm.materialWorkshop = data.workshop || '';
      orderForm.plannedQuantity = data.plannedQuantity || 0;
      
      const images = getImageList(data);
      // 新增：单业务归属禁止将原工单的已确认 ID 复制绑定到新工单。
      // 历史条件：if (images.length > 0)；新工单只带出物料信息，图片需重新上传。
      if (imageVersion === orderImageVersion.value && images.length > 0 && generated === data.productionOrderNo) {
        orderForm.materialImages = images;
        orderForm.materialImageIds = data.imageIds || [];
        orderOriginImageIds.value = [...(data.imageIds || [])];
        // 新增：导入已存在工单时签名只留在编辑内存，不写入业务数据库。
        if (orderForm.materialImageIds.length) Promise.all(orderForm.materialImageIds.map(id => getImagePreviewUrl(id)))
          .then(results => {
            // 历史直接赋值会覆盖新上传；新增：仅当前查询代次允许填入预览。
            if (orderDialogVisible.value && imageVersion === orderImageVersion.value)
              orderForm.materialImages = results.map(result => result.data || '')
          })
          .catch(error => proxy.$modal.msgWarning(error.message));
      } else if (imageVersion === orderImageVersion.value) {
        orderForm.materialImages = [];
        orderForm.materialImageIds = [];
        orderOriginImageIds.value = [];
      }
      
      proxy.$modal.msgSuccess('已自动带出物料信息');
    } else {
      proxy.$modal.msgWarning('未找到该订单号对应的物料信息');
    }
  }).catch(error => {
    console.error('查询订单失败:', error);
    if (error.code === 404 || error.status === 404) {
      proxy.$modal.msgWarning('未找到该订单号对应的物料信息');
    } else {
      proxy.$modal.msgError('查询订单失败，请检查订单号是否正确');
    }
  });
}

// ==================== 日期格式化 ====================
function formatDateToServer(date) {
  if (!date) date = new Date();
  if (typeof date === 'string') {
    date = new Date(date);
  }
  if (isNaN(date.getTime())) {
    date = new Date();
  }
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');
  const seconds = String(date.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
}

function formatDate(dateString) {
  if (!dateString) return '';
  try {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) {
      return dateString;
    }
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const seconds = String(date.getSeconds()).padStart(2, '0');
    return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
  } catch (error) {
    return dateString;
  }
}

// ==================== 提交订单导入 ====================
/** 新增：连续导入保持窗口及物料信息，成功后只清图片并准备下一内部序号。 */
async function submitOrderForm() {
  if (isImporting.value || orderUploading.value > 0 || !orderFormRef.value) return
  isImporting.value = true
  let loadingStarted = false
  try {
    try { await orderFormRef.value.validate() } catch { return }
    const originalNo = orderForm.originalOrderNo.trim()
    if (!originalNo) return
    if (!orderForm.productionOrderNo || checkOrderNoInList(orderForm.productionOrderNo))
      orderForm.productionOrderNo = generateOrderSuffix(originalNo)
    if (checkOrderNoInList(orderForm.productionOrderNo)) {
      proxy.$modal.msgError('无法生成唯一的订单号，请检查订单号是否正确')
      return
    }
    const submitData = {
      productionOrderNo: orderForm.productionOrderNo,
      workshop: orderForm.materialWorkshop,
      materialCode: orderForm.materialCode,
      materialName: orderForm.materialName,
      plannedQuantity: orderForm.plannedQuantity,
      image: orderForm.materialImages.join(','),
      imageIds: [...orderForm.materialImageIds],
      createTime: formatDateToServer(new Date()),
      remark: orderForm.remark
    }
    proxy.$modal.loading('正在导入...')
    loadingStarted = true
    await addProdOrder(submitData)
    importedOrderNos.value.push(submitData.productionOrderNo)
    orderImageVersion.value++
    orderForm.materialImages = []
    orderForm.materialImageIds = []
    orderOriginImageIds.value = []
    // 用户输入的原订单号、物料、车间、数量、备注都保留，仅派生内部编号递增。
    orderForm.productionOrderNo = generateOrderSuffix(originalNo)
    orderNoExists.value = false
    orderFormRef.value.clearValidate()
    proxy.$modal.msgSuccess(`导入成功，订单号：${submitData.productionOrderNo}`)
    getList()
  } catch (error) {
    // 重复编号或其他失败不清空图片，数据库唯一性检查仍是最终防线。
    if (error.message?.includes('Duplicate') || error.message?.includes('重复')) {
      proxy.$modal.msgError('订单号已存在，请重新查询订单号后重试')
      orderNoExists.value = true
    } else proxy.$modal.msgError('导入失败：' + (error.message || ''))
  } finally {
    if (loadingStarted) proxy.$modal.closeLoading()
    isImporting.value = false
  }
}

// 历史实现完整保留：连续导入改造前的 submitOrderForm。
// async function submitOrderForm() {
//   if (orderNoExists.value) {
//     proxy.$modal.msgError(`订单号 "${orderForm.productionOrderNo}" 已存在，将自动生成下一个序号`);
//     const newGenerated = generateOrderSuffix(orderForm.originalOrderNo.trim());
//     orderForm.productionOrderNo = newGenerated;
//     orderNoExists.value = checkOrderNoInList(newGenerated);
//     if (orderNoExists.value) {
//       proxy.$modal.msgError(`无法生成唯一的订单号，请检查订单号是否正确`);
//       return;
//     }
//   }
//   
//   if (!orderFormRef.value) return;
//   
//   try {
//     await orderFormRef.value.validate();
//   } catch (error) {
//     return;
//   }
// 
//   const submitData = {
//     productionOrderNo: orderForm.productionOrderNo,
//     workshop: orderForm.materialWorkshop,
//     materialCode: orderForm.materialCode,
//     materialName: orderForm.materialName,
//     plannedQuantity: orderForm.plannedQuantity,
//     image: orderForm.materialImages.join(','),
//     // 历史实现：imageIds: orderForm.materialImageIds.join(','),
//     imageIds: [...orderForm.materialImageIds],
//     createTime: formatDateToServer(new Date()),
//     remark: orderForm.remark
//   };
// 
//   if (checkOrderNoInList(submitData.productionOrderNo)) {
//     orderNoExists.value = true;
//     proxy.$modal.msgError(`订单号 "${submitData.productionOrderNo}" 已存在，请重新输入订单号`);
//     return;
//   }
// 
//   proxy.$modal.loading("正在导入...");
//   
//   addProdOrder(submitData).then(() => {
//     proxy.$modal.closeLoading();
//     proxy.$modal.msgSuccess(`导入成功，订单号：${submitData.productionOrderNo}`);
//     orderDialogVisible.value = false;
//     resetOrderForm();
//     getList();
//   }).catch((error) => {
//     proxy.$modal.closeLoading();
//     console.error('导入失败:', error);
//     if (error.message?.includes('Duplicate') || error.message?.includes('重复') || error.code === 500) {
//       proxy.$modal.msgError(`订单号 "${submitData.productionOrderNo}" 已存在，请重新输入`);
//     } else {
//       proxy.$modal.msgError('导入失败：' + (error.message || ''));
//     }
//   });
// }

onMounted(() => {
  console.log('组件挂载完成，开始获取数据');
  getList();
});
</script>

<style scoped>
.mb8 {
  margin-bottom: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

/* 新增：弹窗底部只显示一个居中导入按钮，桌面和 PDA 保持相同操作位置。 */
.order-import-footer {
  justify-content: center;
}

.order-import-button {
  min-width: 160px;
  min-height: 44px;
}

.image-upload-container {
  width: 100%;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: flex-start;
}

.image-item {
  position: relative;
  width: 120px;
  height: 120px;
  border-radius: 8px;
  border: 1px solid #dcdfe6;
  overflow: hidden;
  background-color: transparent !important;
}

.image-item .avatar {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background-color: transparent;
}

.image-actions {
  position: absolute;
  top: 4px;
  right: 4px;
  display: flex;
  gap: 4px;
}

.image-actions .el-button {
  width: 24px;
  height: 24px;
  padding: 0;
  font-size: 12px;
}

.avatar-uploader {
  width: 120px;
  height: 120px;
  border: 1px dashed var(--el-border-color);
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: transparent !important;
  transition: border-color 0.3s;
}

.avatar-uploader:hover {
  border-color: var(--el-color-primary);
}

/* 新增：整块上传区域均可触摸，避免 PDA 只能点中间的小加号。 */
.avatar-uploader :deep(.el-upload) {
  width: 100%;
  height: 100%;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: var(--el-text-color-placeholder);
}

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}

.table-image-list {
  /* 历史布局：display:flex; flex-wrap:wrap; gap:2px。新增：最多四张固定单行，避免行高跳动。 */
  display: grid;
  grid-template-columns: repeat(4, 40px);
  justify-content: center;
  align-items: center;
  gap: 4px;
}

/* 新增：单个查看入口或无图提示不偏向四列布局的左侧。 */
.table-image-list > :only-child {
  grid-column: 1 / -1;
  justify-self: center;
}

/* 新增：工具栏随可用宽度换行，避免 PDA 按钮挤出页面。 */
.order-toolbar {
  row-gap: 8px;
  align-items: center;
}

.order-toolbar :deep(.top-right-btn) {
  margin-left: auto;
}

:deep(.el-input-number .el-input__inner) {
  text-align: left;
}

@media (max-width: 768px) {
  /* 新增：PDA 搜索项逐行排列，字段和操作按钮均提供足够触摸区域。 */
  .order-image-page {
    padding: 12px;
    min-width: 0;
  }

  .order-search :deep(.el-form-item) {
    display: flex;
    width: 100%;
    margin-right: 0;
    margin-bottom: 12px;
  }

  .order-search :deep(.el-form-item__content) {
    flex: 1;
    min-width: 0;
  }

  .order-search :deep(.el-input),
  .order-search :deep(.el-button),
  .order-toolbar :deep(.el-button) {
    min-height: 44px;
  }

  /* 新增：工具栏列按按钮实际宽度换行，圆形搜索/刷新也保留 44px 触摸宽度。 */
  .order-toolbar > :deep(.el-col) {
    flex: 0 0 auto;
    width: auto;
    max-width: none;
  }

  .order-toolbar :deep(.el-button.is-circle) {
    width: 44px;
  }

  .order-search :deep(.el-form-item:last-child .el-form-item__content) {
    display: flex;
    gap: 8px;
  }

  .order-search :deep(.el-form-item:last-child .el-button) {
    flex: 1;
    margin-left: 0;
  }

  .order-image-page :deep(.pagination-container) {
    height: auto;
    padding: 12px 0;
  }

  .order-image-page :deep(.el-pagination) {
    justify-content: center;
    flex-wrap: wrap;
  }

  /* 新增：图片删除按钮在触屏上直接可点，不依赖鼠标悬停。 */
  .image-list {
    gap: 10px;
  }

  .image-item,
  .avatar-uploader {
    /* 历史触屏图片尺寸：80px；新增：96px 便于预览并容纳触摸删除按钮。 */
    width: 96px;
    height: 96px;
  }

  .image-actions .el-button {
    width: 40px;
    height: 40px;
    font-size: 16px;
  }
}
</style>

<!-- 新增：仅此页面的弹窗使用唯一类名，全局规则避免 append-to-body 导致 scoped 样式失效。 -->
<style>
@media (max-width: 768px) {
  /* 新增：弹窗传送到 body，使用页面专属根类限定全局样式，适配编辑与订单导入。 */
  .order-image-dialog {
    margin: 12px auto;
    max-width: calc(100vw - 24px);
    padding: 12px;
  }

  .order-image-dialog .el-dialog__body {
    max-height: calc(100vh - 160px);
    max-height: calc(100dvh - 160px);
    overflow-y: auto;
  }

  .order-image-dialog .el-form-item {
    /* 历史 PDA 布局：display:block；新增：标签与输入区始终保持同一行。 */
    display: flex;
    flex-wrap: nowrap;
    align-items: flex-start;
    margin-bottom: 16px;
  }

  .order-image-dialog .el-form-item__label {
    /* 历史布局：display:block; width:auto; height:auto; padding:0 0 6px; line-height:24px。 */
    /* 新增：统一 80px 标签列，44px 高度与可触摸输入框对齐，内容区占用剩余宽度。 */
    display: flex;
    align-items: center;
    justify-content: flex-start;
    flex: 0 0 80px;
    float: none;
    width: 80px !important;
    height: 44px;
    padding: 0 8px 0 0;
    text-align: left;
    line-height: 20px;
  }

  .order-image-dialog .el-form-item__content {
    /* 新增：允许窄屏输入框收缩，覆盖两个表单原有的不同 label-width。 */
    flex: 1;
    margin-left: 0 !important;
    min-width: 0;
  }

  .order-image-dialog .el-form-item__content > .el-input,
  .order-image-dialog .el-form-item__content > .el-input-number {
    width: 100%;
    min-width: 0;
  }

  .order-image-dialog .el-input__wrapper,
  .order-image-dialog .el-input-number {
    min-height: 44px;
  }

  .order-image-dialog .dialog-footer {
    display: flex;
    gap: 12px;
  }

  .order-image-dialog .dialog-footer .el-button {
    flex: 1;
    min-height: 44px;
    margin-left: 0;
  }

  /* 新增：覆盖历史双按钮等分布局，单个导入按钮保持居中且不超过窄屏宽度。 */
  .order-image-dialog .order-import-footer {
    justify-content: center;
  }

  .order-image-dialog .order-import-footer .el-button {
    flex: 0 1 240px;
    min-width: 140px;
    max-width: 100%;
  }

}
</style>
