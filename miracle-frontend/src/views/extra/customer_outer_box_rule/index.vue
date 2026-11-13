<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="客户编码" prop="customerCode">
        <el-input
          v-model="queryParams.customerCode"
          placeholder="请输入客户编码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="客户名称" prop="customerName">
        <el-input
          v-model="queryParams.customerName"
          placeholder="请输入客户名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="业务字段" prop="fieldName">
        <el-input
          v-model="queryParams.fieldName"
          placeholder="请输入业务字段名"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="启用状态" prop="activeStatus">
        <el-select v-model="queryParams.activeStatus" placeholder="请选择" clearable style="width: 200px">
          <el-option label="启用" :value="1" />
          <el-option label="停用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete">删除</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="ruleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="60" />
      <el-table-column label="客户编码" align="center" prop="customerCode" width="100" />
      <el-table-column label="客户名称" align="center" prop="customerName" width="120" />
      <el-table-column label="客户标签标识" align="center" prop="customerFlag" width="120" />
      <el-table-column label="业务字段名" align="center" prop="fieldName" width="160" />
      <el-table-column label="字段中文名" align="center" prop="fieldLabel" width="120" />
      <el-table-column label="正则表达式" align="center" prop="labelRegex" show-overflow-tooltip min-width="200" />
      <el-table-column label="组号" align="center" prop="extractGroup" width="60" />
      <el-table-column label="校验字段" align="center" width="90">
        <template #default="scope">
          <el-tag
            :type="scope.row.fieldName === 'customerPartNumber' ? 'success' : 'info'"
            size="small"
          >
            {{ scope.row.fieldName === 'customerPartNumber' ? '是' : '否' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sortOrder" width="60" />
      <el-table-column label="启用状态" align="center" prop="activeStatus" width="80">
        <template #default="scope">
          <el-tag :type="scope.row.activeStatus === 1 ? 'success' : 'danger'" size="small">
            {{ scope.row.activeStatus === 1 ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="160" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button type="primary" link icon="Edit" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button type="danger" link icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 新增/修改对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.show" width="640px" append-to-body>
      <el-form ref="ruleRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="客户编码" prop="customerCode">
          <el-input v-model="form.customerCode" placeholder="如：BYD" />
        </el-form-item>
        <el-form-item label="客户名称" prop="customerName">
          <el-input v-model="form.customerName" placeholder="如：比亚迪" />
        </el-form-item>
        <el-form-item label="客户标签标识" prop="customerFlag">
          <el-input v-model="form.customerFlag" placeholder="如：BYDOutBox（对应 vwOutBoxLabelRecord.CustomerFlag）" />
        </el-form-item>
        <el-form-item label="业务字段名" prop="fieldName">
          <el-input v-model="form.fieldName" placeholder="如：customerPartNumber、lotNumber、manufactureDate" />
          <div style="font-size: 12px; color: #909399; margin-top: 5px;">
            约定命名：<b>customerPartNumber</b> = 客户料号（作为与出货标签比对的校验字段）<br/>
            其他常用：lotNumber 批次号、manufactureDate 生产日期、quantity 数量等
          </div>
        </el-form-item>
        <el-form-item label="字段中文名" prop="fieldLabel">
          <el-input v-model="form.fieldLabel" placeholder="如：客户料号、批次号、生产日期" />
        </el-form-item>
        <el-form-item label="正则表达式" prop="labelRegex">
          <el-input v-model="form.labelRegex" type="textarea" :rows="2" placeholder="如：M:([^/;]+)" />
          <div style="font-size: 12px; color: #909399; margin-top: 5px;">
            示例标签：P:S0X0;V:133341;M:20661942-00/PCS;B:2604302EG2;...<br/>
            正则 M:([^/;]+) 将提取出 20661942-00
          </div>
        </el-form-item>
        <el-form-item label="提取组号" prop="extractGroup">
          <el-input-number v-model="form.extractGroup" :min="1" :max="9" />
          <div style="font-size: 12px; color: #909399; margin-top: 5px;">
            正则若有多个捕获组，指定取第N组（默认1）
          </div>
        </el-form-item>
        <el-form-item label="排序号" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="启用状态" prop="activeStatus">
          <el-radio-group v-model="form.activeStatus">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="dialog.show = false">取 消</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="CustomerOuterBoxRule">
import { ref, reactive, getCurrentInstance } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  listOuterBoxRule,
  addOuterBoxRule,
  updateOuterBoxRule,
  delOuterBoxRule
} from '@/api/extra/customer_outer_box_rule.js'

const { proxy } = getCurrentInstance()

const ruleList = ref([])
const loading = ref(true)
const showSearch = ref(true)
const ids = ref([])
const single = ref(true)
const multiple = ref(true)
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  customerCode: undefined,
  customerName: undefined,
  fieldName: undefined,
  activeStatus: undefined
})

const dialog = reactive({
  show: false,
  title: ''
})

const form = reactive({
  id: undefined,
  customerCode: '',
  customerName: '',
  customerFlag: '',
  fieldName: '',
  fieldLabel: '',
  labelRegex: '',
  extractGroup: 1,
  sortOrder: 0,
  activeStatus: 1,
  remark: ''
})

const rules = reactive({
  customerCode: [{ required: true, message: '客户编码不能为空', trigger: 'blur' }],
  customerName: [{ required: true, message: '客户名称不能为空', trigger: 'blur' }],
  fieldName: [{ required: true, message: '业务字段名不能为空', trigger: 'blur' }],
  labelRegex: [{ required: true, message: '正则表达式不能为空', trigger: 'blur' }]
})

function getList() {
  loading.value = true
  listOuterBoxRule(queryParams).then(res => {
    ruleList.value = res.rows
    total.value = res.total
    loading.value = false
  })
}

function handleQuery() {
  queryParams.pageNum = 1
  getList()
}

function resetQuery() {
  proxy.resetForm('queryRef')
  handleQuery()
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  single.value = selection.length !== 1
  multiple.value = !selection.length
}

function reset() {
  form.id = undefined
  form.customerCode = ''
  form.customerName = ''
  form.customerFlag = ''
  form.fieldName = ''
  form.fieldLabel = ''
  form.labelRegex = ''
  form.extractGroup = 1
  form.sortOrder = 0
  form.activeStatus = 1
  form.remark = ''
  proxy.resetForm('ruleRef')
}

function handleAdd() {
  reset()
  dialog.title = '新增客户规则'
  dialog.show = true
}

function handleUpdate(row) {
  reset()
  const id = row.id || ids.value[0]
  const data = row.id ? row : ruleList.value.find(item => item.id === id)
  Object.assign(form, data)
  dialog.title = '修改客户规则'
  dialog.show = true
}

function submitForm() {
  proxy.$refs['ruleRef'].validate(valid => {
    if (valid) {
      if (form.id != null) {
        updateOuterBoxRule(form).then(() => {
          ElMessage.success('修改成功')
          dialog.show = false
          getList()
        })
      } else {
        addOuterBoxRule(form).then(() => {
          ElMessage.success('新增成功')
          dialog.show = false
          getList()
        })
      }
    }
  })
}

function handleDelete(row) {
  const deleteIds = row.id ? [row.id] : ids.value
  ElMessageBox.confirm('是否确认删除选中的客户规则？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    return delOuterBoxRule(deleteIds.join(','))
  }).then(() => {
    getList()
    ElMessage.success('删除成功')
  }).catch(() => {})
}

getList()
</script>
