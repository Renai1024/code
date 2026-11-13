<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
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
<!--      <el-form-item label="后工序车间" prop="materialWorkshop">-->
<!--        <el-input-->
<!--            v-model="queryParams.materialWorkshop"-->
<!--            placeholder="请输入后工序车间"-->
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
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--            type="primary"-->
<!--            plain-->
<!--            icon="Plus"-->
<!--            @click="handleAdd"-->
<!--            v-hasPermi="['extra:mes_transpose_material:add']"-->
<!--        >新增</el-button>-->
<!--      </el-col>-->
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleEdit"
            v-hasPermi="['extra:mes_transpose_material:edit']"
        >修改</el-button>
      </el-col>
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--            type="danger"-->
<!--            plain-->
<!--            icon="Delete"-->
<!--            :disabled="multiple"-->
<!--            @click="handleDelete"-->
<!--            v-hasPermi="['extra:mes_transpose_material:remove']"-->
<!--        >删除</el-button>-->
<!--      </el-col>-->
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
        >导出</el-button>
      </el-col>
<!--      <el-col :span="1.5">-->
<!--        <el-button-->
<!--            type="info"-->
<!--            plain-->
<!--            icon="Upload"-->
<!--            @click="handleBatchAdd"-->
<!--            v-hasPermi="['extra:mes_transpose_material:addBatch']"-->
<!--        >批量导入</el-button>-->
<!--      </el-col>-->
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading"
              :data="dataList"
              @selection-change="handleSelectionChange"
              @row-click="handleRowClick"
              ref="tableRef">
      <el-table-column type="selection" width="40" align="center" />
      <el-table-column label="物料编码" align="center" prop="materialCode" min-width="140" show-overflow-tooltip/>
      <el-table-column label="物料名称" align="center" prop="materialName" min-width="180" show-overflow-tooltip/>
<!--      <el-table-column label="后工序车间" align="center" prop="materialWorkshop" min-width="160" show-overflow-tooltip/>-->
      <el-table-column label="物料图片" align="center" min-width="120" show-overflow-tooltip>
        <template #default="scope">
          <!-- 历史原图列表实现保留：
<el-image
              v-if="scope.row.materialImage"
              :src="scope.row.materialImage"
              :preview-src-list="[scope.row.materialImage]"
              fit="cover"
              style="width: 60px; height: 60px"
              preview-teleported="true"
          />
              -->
              <!-- 历史绑定：:thumbnail="thumbnailUrls[scope.row.materialImageId]"。
                   新增：物料响应直接提供 public thumbnailUrl，materialImage 仅供主动预览。 -->
              <BusinessImage v-if="scope.row.materialImageId || scope.row.materialImage" :size="60"
                :image-id="scope.row.materialImageId" :thumbnail="scope.row.thumbnailUrl"
                :fallback="scope.row.materialImage" @error="error => proxy.$modal.msgError(error.message)" />
          <span v-else>无</span>
        </template>
      </el-table-column>
      <el-table-column label="创建人" align="center" prop="createBy" min-width="120" show-overflow-tooltip/>
      <el-table-column label="创建时间" align="center" prop="createTime" min-width="180" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ formatDate(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新人" align="center" prop="updateBy" min-width="120" show-overflow-tooltip/>
      <el-table-column label="更新时间" align="center" prop="updateTime" min-width="180" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ formatDate(scope.row.updateTime) }}</span>
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

    <!-- 添加或修改转序物料对话框 -->
    <el-dialog :title="title" v-model="open" width="90%" append-to-body class="dialog-edit mobile-dialog">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="物料编码" prop="materialCode">
          <el-input v-model="form.materialCode" placeholder="请输入物料编码" readonly />
        </el-form-item>
        <el-form-item label="物料名称" prop="materialName">
          <el-input v-model="form.materialName" placeholder="请输入物料名称" readonly/>
        </el-form-item>
        <el-form-item label="后工序车间" prop="materialWorkshop">
          <el-input v-model="form.materialWorkshop" placeholder="请输入后工序车间" readonly/>
        </el-form-item>
        <el-form-item label="物料图片" prop="materialImage">
          <div class="image-upload-container">
            <el-upload
                class="avatar-uploader"
                action=""
                :show-file-list="false"
                :on-change="handleImageUpload"
                :auto-upload="false"
                accept="image/*"
                :before-upload="handleBeforeUpload"
            >
              <img v-if="form.materialImage" :src="form.materialImage" class="avatar" />
              <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
            </el-upload>
            <el-button
                v-if="form.materialImage"
                class="delete-image-btn"
                type="danger"
                icon="Delete"
                size="small"
                @click.prevent="handleImageDelete"
            >删除</el-button>
          </div>
          <div class="upload-tip">点击上传物料图片，支持更换或删除已上传图片</div>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input
              v-model="form.remark"
              placeholder="请输入备注"
              type="textarea"
              :rows="3"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer mobile-dialog-footer">
          <el-button type="primary" @click="submitForm" class="submit-btn">确 定</el-button>
          <el-button @click="cancel" class="cancel-btn">取 消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 批量导入转序物料对话框 -->
<!--    <el-dialog-->
<!--        title="批量导入转序物料"-->
<!--        v-model="batchDialogVisible"-->
<!--        width="500px"-->
<!--        append-to-body-->
<!--    >-->
<!--      <el-upload-->
<!--          class="upload-demo"-->
<!--          action=""-->
<!--          :auto-upload="false"-->
<!--          :on-change="handleFileChange"-->
<!--          :show-file-list="true"-->
<!--          accept=".xlsx,.xls"-->
<!--          multiple-->
<!--      >-->
<!--        <el-button type="primary">选择文件</el-button>-->
<!--        <template #tip>-->
<!--          <div class="el-upload__tip">-->
<!--            请上传Excel文件，支持.xlsx和.xls格式-->
<!--          </div>-->
<!--        </template>-->
<!--      </el-upload>-->
<!--      <template #footer>-->
<!--        <div class="dialog-footer">-->
<!--          <el-button @click="batchDialogVisible = false">取 消</el-button>-->
<!--          <el-button type="primary" @click="handleBatchSubmit">确 定 导 入</el-button>-->
<!--        </div>-->
<!--      </template>-->
<!--    </el-dialog>-->
  </div>
</template>

<script setup name="Mes_transpose_material">
// 新增：物料列表直接携带 public 缩略图地址，点击时才加载原图。
import BusinessImage from '@/components/BusinessImage/index.vue'
// 历史额外查询保留：import { useBusinessThumbnails } from '@/composables/useBusinessThumbnails'
// const { thumbnailUrls, refreshThumbnails } = useBusinessThumbnails()

import { Plus } from '@element-plus/icons-vue'
import { ref, reactive, toRefs, onMounted } from 'vue'
import {
  list_transpose_material,
  get_transpose_material,
  add_transpose_material,
  addBatch_transpose_material,
  update_transpose_material,
  del_transpose_material
} from "@/api/extra/mes_transpose_material.js"
// 历史导入：import {deleteImage, uploadImage}
import {uploadImage, getImagePreviewUrl} from "@/api/extra/image_store.js"
import {search_material, search_workshop} from "@/api/extra/mes_workshop_transpose.js";

// 测试：打印模块加载
console.log('Mes_transpose_material module loaded');

const { proxy } = getCurrentInstance();

// 响应式数据
const dataList = ref([]);
const open = ref(false);
const batchDialogVisible = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");
const uploadFiles = ref([]);
const imageUrl = ref('');
const formRef = ref(null);
const queryRef = ref(null);
const originImageId = ref(null);

const tableRef = ref(null);

// 表单数据
const data = reactive({
  form: {
    id: null,
    materialCode: '',
    materialName: '',
    materialWorkshop: '',
    materialImage: '',
    materialImageId: null, // 新增：Long ID 空值提交 null。
    remark: ''
  },
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    materialCode: '',
    materialName: '',
    materialWorkshop: ''
  },
  rules: {
    materialCode: [
      { required: true, message: '请输入物料编码', trigger: 'blur' }
    ]
  }
});

const { queryParams, form, rules } = toRefs(data);



/** 查询转序物料列表 */
function getList() {
  loading.value = true;
  list_transpose_material(queryParams.value).then(response => {
    dataList.value = response.rows;
    // 历史额外请求：refreshThumbnails(dataList.value.map(row => row.materialImageId)).catch(error => proxy.$modal.msgWarning(error.message));
    // 新增：直接使用 response.rows 中的 thumbnailUrl，不签名、不再请求 thumbnail-urls。
    total.value = response.total;
    loading.value = false;
    console.log('获取数据列表成功:', response.rows.length, '条记录');
  }).catch(error => {
    loading.value = false;
    console.error('获取数据列表失败:', error);
    proxy.$modal.msgError('获取数据失败');
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    id: null,
    materialCode: '',
    materialName: '',
    materialWorkshop: '',
    materialImage: '',
    materialImageId: null, // 新增：Long ID 空值提交 null。
    remark: ''
  };
  if (formRef.value) {
    formRef.value.resetFields();
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
  ids.value = selection.map(item => item.id);
  single.value = selection.length !== 1;
  multiple.value = selection.length === 0;
  console.log('选择状态更新:', {
    ids: ids.value,
    single: single.value,
    multiple: multiple.value
  });
}

// 点击行选中
function handleRowClick(row) {
  const tableRef = proxy.$refs.tableRef || document.querySelector('.el-table');
  if (tableRef) {
    tableRef.toggleRowSelection(row);
  }
}

/** 新增按钮操作 */
function handleAdd() {
  console.log('点击新增按钮');
  reset();
  open.value = true;
  title.value = "添加转序物料";
  console.log('对话框状态:', open.value);
}

/** 修改按钮操作 */
function handleEdit() {
  console.log('点击修改按钮');
  console.log('当前选择ID:', ids.value);

  if (ids.value.length === 0) {
    console.log('未选择记录');
    proxy.$modal.msgWarning('请选择一条记录进行修改');
    return;
  }

  if (ids.value.length > 1) {
    console.log('选择了多条记录');
    proxy.$modal.msgWarning('只能选择一条记录进行修改');
    return;
  }

  reset();
  const _id = ids.value[0];
  console.log('修改ID:', _id);

  // 实际API调用
  get_transpose_material(_id).then(response => {
    console.log('获取数据成功:', response);
    if (response && response.data) {
      console.log('数据:', response.data);
      form.value = response.data;
      originImageId.value = response.data.materialImageId;
      // 新增：编辑页通过权限接口签名原图，不依赖私有桶的稳定地址。
      if (form.value.materialImageId) getImagePreviewUrl(form.value.materialImageId).then(result => {
        if (form.value.materialImageId === response.data.materialImageId) form.value.materialImage = result.data || '';
      }).catch(error => proxy.$modal.msgWarning(error.message));
      // 根据物料编码查询后工序车间
      if (form.value.materialCode) {
        search_material({
          searchText: form.value.materialCode,
          pageNum: 1,
          pageSize: 1
        }).then(materialRes => {
          let materialData = null;
          if (materialRes.data && materialRes.data.length > 0) {
            materialData = materialRes.data[0];
          } else if (materialRes.rows && materialRes.rows.length > 0) {
            materialData = materialRes.rows[0];
          }

          if (materialData) {
            const workshopCode = materialData.materialWorkshop || '';
            if (!form.value.materialImage) {
              form.value.materialImage = materialData.materialImage || '';
            }

            // 如果有车间编码，查询车间名称
            if (workshopCode) {
              search_workshop({
                searchText: workshopCode,
                pageNum: 1,
                pageSize: 1
              }).then(workshopRes => {
                let workshopData = null;
                if (workshopRes.data && workshopRes.data.length > 0) {
                  workshopData = workshopRes.data[0];
                } else if (workshopRes.rows && workshopRes.rows.length > 0) {
                  workshopData = workshopRes.rows[0];
                }

                if (workshopData) {
                  // 显示车间名称
                  form.value.materialWorkshop = workshopData.name || workshopData.workshopName || workshopCode;
                } else {
                  form.value.materialWorkshop = workshopCode;
                }
              }).catch(err => {
                console.error('获取车间名称失败:', err);
                form.value.materialWorkshop = workshopCode;
              });
            }
          }
        }).catch(err => {
          console.error('获取物料后工序车间失败:', err);
        });
      }

      open.value = true;
      title.value = "修改转序物料";
      console.log('对话框状态:', open.value);
    } else {
      console.error('获取数据格式错误:', response);
      proxy.$modal.msgError('获取数据格式错误');
    }
  }).catch(error => {
    console.error('获取数据失败:', error);
    proxy.$modal.msgError('获取数据失败: ' + (error.message || '未知错误'));
  });
}

/** 提交按钮 */
function submitForm() {
  if (!formRef.value) return;

  formRef.value.validate(valid => {
    if (valid) {
      if (form.value.id != null) {
        update_transpose_material(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        }).catch(error => {
          proxy.$modal.msgError('修改失败');
        });
      } else {
        add_transpose_material(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        }).catch(error => {
          proxy.$modal.msgError('新增失败');
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete() {
  if (ids.value.length === 0) {
    proxy.$modal.msgWarning('请选择至少一条记录进行删除');
    return;
  }

  const _ids = ids.value;
  proxy.$modal.confirm('是否确认删除选中的转序物料数据？').then(function() {
    return del_transpose_material(_ids);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('extra/mes/report/mes_transpose_material/export', {
    ...queryParams.value
  }, `转序物料数据_${new Date().getTime()}.xlsx`)
}

/** 批量导入按钮操作 */
function handleBatchAdd() {
  uploadFiles.value = [];
  batchDialogVisible.value = true;
}

/** 文件选择变更 */
function handleFileChange(file, fileList) {
  uploadFiles.value = fileList;
}

/** 上传前校验 */
function handleBeforeUpload(file) {
  // 历史类型：JPEG/PNG/SVG/PDF；后端仅支持 JPEG/PNG，手动上传必须显式验证。
  const type = ["image/jpeg", "image/jpg", "image/png"];
  const isJPG = type.includes(file.type);
  //检验文件格式
  if (!isJPG) {
    proxy.$modal.msgError(`图片格式错误!`);
    return false;
  }
  // 校验文件大小
  const isLt = file.size / 1024 / 1024 < 5;
  if (!isLt) {
    proxy.$modal.msgError(`上传文件大小不能超过 5 MB!`);
    return false;
  }
  return true;
}

/** 图片上传处理 */
function handleImageUpload(file) {
  /*// 检查是否存在旧图片，如果存在则先删除
  if (form.value.materialImageId) {
    // 先删除旧图片
    deleteImage(form.value.materialImageId).then(deleteResponse => {
      if (deleteResponse.code === 200) {
        console.log('旧图片删除成功');
      } else {
        console.log('旧图片删除失败，但继续上传新图片：', deleteResponse.msg);
      }
      // 无论旧图片是否删除成功，都继续上传新图片
      uploadImageActual(file);

    }).catch(deleteError => {
      console.log('旧图片删除出错，但继续上传新图片：', deleteError.message);
      // 即使删除失败，也继续上传新图片
      uploadImageActual(file);
    });
  } else {
    // 没有旧图片，直接上传新图片
    //uploadImage(file);
    uploadImageActual(file)
    
  }*/
  // 历史实现保留：替换时立即 deleteImage(form.value.materialImageId)。
  // 新增：连续替换只修改表单；未提交暂存图由到期任务回收。
  if (!handleBeforeUpload(file.raw || file)) return;
  uploadImageActual(file);
}

/** 图片实际上传 */
function uploadImageActual(file) {
  const source = file.raw || file
  proxy.$modal.loading('正在上传图片，请稍候...')
  uploadImage(source, 'MATERIAL').then(response => {
    if (response.code !== 200 || !response.data?.imageId || !response.data?.imageUrl) {
      throw new Error(response.msg || '服务端未返回图片信息')
    }
    form.value.materialImage = response.data.imageUrl
    form.value.materialImageId = response.data.imageId
    proxy.$modal.msgSuccess('图片上传成功')
  }).catch(error => {
    proxy.$modal.msgError('图片上传失败：' + error.message)
  }).finally(() => proxy.$modal.closeLoading())
}

/** 批量提交操作 */
function handleBatchSubmit() {
  if (uploadFiles.value.length === 0) {
    proxy.$modal.msgWarning('请选择文件');
    return;
  }

  // 这里可以添加文件解析逻辑，将Excel文件解析为数据
  // 暂时模拟批量导入
  // const batchData = [
  //   {
  //     materialCode: 'TEST001',
  //     materialName: '测试物料1',
  //     materialWorkshop: '车间1',
  //     materialImage: '',
  //     remark: '批量导入测试'
  //   },
  //   {
  //     materialCode: 'TEST002',
  //     materialName: '测试物料2',
  //     materialWorkshop: '车间2',
  //     materialImage: '',
  //     remark: '批量导入测试'
  //   }
  // ];

  addBatch_transpose_material(batchData).then(response => {
    proxy.$modal.msgSuccess('批量导入成功');
    batchDialogVisible.value = false;
    getList();
  }).catch(error => {
    proxy.$modal.msgError('批量导入失败');
  });
}

/** 图片删除处理 */
function handleImageDelete() {
  proxy.$modal.confirm('确定要删除当前物料图片吗？').then(() => {
      //proxy.$modal.closeLoading();
      form.value.materialImage = '';
      form.value.materialImageId = null; // 新增：明确清空 nullable 图片 ID。
      proxy.$modal.msgSuccess('图片删除成功');
  }).catch(() => {});
}

/** 日期格式化 */
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

// 初始化
onMounted(() => {
  console.log('组件挂载完成，开始获取数据');
  getList();
});
</script>

<style scoped>
.dialog-edit{
  max-width: 90%;
  min-width: 300px;
}

/* 移动端对话框样式 */
.mobile-dialog :deep(.el-dialog__body) {
  padding: 15px 20px;
}

.mobile-dialog-footer {
  text-align: center;
}

.submit-btn, .cancel-btn {
  width: 45%;
  margin: 0 5px;
}

.mb8 {
  margin-bottom: 8px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.upload-demo {
  margin: 20px 0;
}

.image-upload-container {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.avatar-uploader {
  display: flex;
  align-items: center;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 8px;
  object-fit: cover;
  cursor: pointer;
}

.avatar-uploader-icon {
  width: 120px;
  height: 120px;
  line-height: 120px;
  border: 1px dashed var(--el-border-color);
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  font-size: 24px;
  color: var(--el-text-color-placeholder);
}

.delete-image-btn {
  margin-top: 8px;
}

.upload-tip {
  margin-top: 8px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
}


@media (max-width: 768px){
  .dialog-edit{
    width: 95%;
    max-width: 95%;
  }

  .avatar,
  .avatar-uploader-icon {
    width: 100px;
    height: 100px;
    line-height: 100px;
  }
}
</style>
