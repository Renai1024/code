<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="记录时间范围" prop="createTimeRange">
        <el-date-picker
          v-model="createTimeRange"
          type="datetimerange"
          range-separator="至"
          start-placeholder="开始时间"
          end-placeholder="结束时间"
          value-format="YYYY-MM-DD HH:mm:ss"
          @change="handleCreateTimeRangeChange"
          clearable
          :unlink-panels="true"
        />
      </el-form-item>
      <el-form-item label="条码" prop="batchNo">
        <el-input
          v-model="queryParams.batchNo"
          placeholder="请输入产品条码"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="产品料号" prop="productCode">
        <el-input
          v-model="queryParams.productCode"
          placeholder="请输入产品料号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="厂家设备编号" prop="equipmentNumber">
        <el-input
          v-model="queryParams.equipmentNumber"
          placeholder="请输入厂家设备编号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="内部设备编号" prop="EquipmentNo">
        <el-input
          v-model="queryParams.EquipmentNo"
          placeholder="请输入内部设备编号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <!-- <el-form-item label="报警状态" prop="alarmStatus">
        <el-input
          v-model="queryParams.alarmStatus"
          placeholder="请输入报警状态"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item> -->
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

    <el-table v-loading="loading" :data="chuXinDuanZiHanList" @selection-change="handleSelectionChange" max-height="600">
      <el-table-column type="selection" min-width="40" align="center" fixed />
      <el-table-column label="记录时间" align="center" prop="createTime" min-width="160" show-overflow-tooltip fixed>
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="条码" align="center" prop="batchNo" min-width="120" show-overflow-tooltip/>
      <el-table-column label="生产时间" align="center" prop="productionTime" min-width="160" show-overflow-tooltip/>
      <el-table-column label="产品料号" align="center" prop="productCode" min-width="130" show-overflow-tooltip/>
      <el-table-column label="焊接时间(ms)" align="center" prop="weldingTime" min-width="100" show-overflow-tooltip/>
      <el-table-column label="焊接振幅(%)" align="center" prop="weldingAmplitude" min-width="100" show-overflow-tooltip/>
      <el-table-column label="焊接压力(Mpa)" align="center" prop="weldingPressure" min-width="100" show-overflow-tooltip/>
      <el-table-column label="焊接能量(J)" align="center" prop="weldingEnergy" min-width="100" show-overflow-tooltip/>
      <el-table-column label="焊接功率(W)" align="center" prop="weldingPower" min-width="100" show-overflow-tooltip/>
      <el-table-column label="预压高度(mm)" align="center" prop="preloadingHeight" min-width="100" show-overflow-tooltip/>
      <el-table-column label="焊后高度(mm)" align="center" prop="postWeldingHeight" min-width="100" show-overflow-tooltip/>
      <el-table-column label="焊接深度(mm)" align="center" prop="weldingDepth" min-width="100" show-overflow-tooltip/>
      <el-table-column label="操作模式" align="center" prop="operationMode" min-width="100" show-overflow-tooltip/>
      <el-table-column label="工作模式" align="center" prop="workingMode" min-width="100" show-overflow-tooltip/>
      <el-table-column label="时间上限(ms)" align="center" prop="timeUpperLimit" min-width="100" show-overflow-tooltip/>
      <el-table-column label="时间下限(ms)" align="center" prop="timeLowerLimit" min-width="100" show-overflow-tooltip/>
      <el-table-column label="功率上限(W)" align="center" prop="powerUpperLimit" min-width="100" show-overflow-tooltip/>
      <el-table-column label="功率下限(W)" align="center" prop="powerLowerLimit" min-width="100" show-overflow-tooltip/>
      <el-table-column label="能量上限(J)" align="center" prop="energyUpperLimit" min-width="100" show-overflow-tooltip/>
      <el-table-column label="能量下限(J)" align="center" prop="energyLowerLimit" min-width="100" show-overflow-tooltip/>
      <el-table-column label="预压上限(mm)" align="center" prop="preloadingUpperLimit" min-width="100" show-overflow-tooltip/>
      <el-table-column label="预压下限(mm)" align="center" prop="preloadingLowerLimit" min-width="100" show-overflow-tooltip/>
      <el-table-column label="焊后上限(mm)" align="center" prop="postWeldingUpperLimit" min-width="100" show-overflow-tooltip/>
      <el-table-column label="焊后下限(mm)" align="center" prop="postWeldingLowerLimit" min-width="100" show-overflow-tooltip/>
      <el-table-column label="焊头代码" align="center" prop="hornCode" min-width="120" show-overflow-tooltip/>
      <el-table-column label="砧座代码" align="center" prop="anvilCode" min-width="120" show-overflow-tooltip/>
      <!-- <el-table-column label="夹具代码" align="center" prop="holderCode" min-width="120" show-overflow-tooltip/> -->
      <!-- <el-table-column label="焊头A使用次数" align="center" prop="hornAUseCount" min-width="120" show-overflow-tooltip/> -->
      <!-- <el-table-column label="焊头B使用次数" align="center" prop="hornBUseCount" min-width="120" show-overflow-tooltip/> -->
      <el-table-column label="焊头使用次数" align="center" prop="hornUseCount" min-width="120" show-overflow-tooltip/>
      <el-table-column label="砧座使用次数" align="center" prop="anvilUseCount" min-width="120" show-overflow-tooltip/>
      <!-- <el-table-column label="夹具使用次数" align="center" prop="holderUseCount" min-width="120" show-overflow-tooltip/> -->
      <el-table-column label="报警状态" align="center" prop="alarmStatus" min-width="100" show-overflow-tooltip/>
      <el-table-column label="厂家设备编号" align="center" prop="equipmentNumber" min-width="170" show-overflow-tooltip fixed="right" />
      <el-table-column label="内部设备编号" align="center" prop="equipmentNo" min-width="120" show-overflow-tooltip fixed="right" />
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page-sizes="[20, 50, 100, 200]"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script setup name="MesReportChuXinDuanZiHan">
import { ref, reactive, toRefs } from "vue";
import { getCurrentInstance } from "vue";
import { mes_report_chuXinDuanZiHan } from "@/api/extra/mes_report_chuXinDuanZiHan";

const { proxy } = getCurrentInstance();

const chuXinDuanZiHanList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const createTimeRange = ref([]);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 20,
    batchNo: null,
    productCode: null,
    equipmentNumber: null,
    equipmentNo:null,
    alarmStatus: null,
    createStartTime: null,
    createEndTime: null
  }
});

const { queryParams } = toRefs(data);

function handleCreateTimeRangeChange(range) {
  if (range && range.length === 2) {
    queryParams.value.createStartTime = range[0];
    queryParams.value.createEndTime = range[1];
  } else {
    queryParams.value.createStartTime = null;
    queryParams.value.createEndTime = null;
  }
}

function getList() {
  loading.value = true;
  mes_report_chuXinDuanZiHan(queryParams.value).then(response => {
    chuXinDuanZiHanList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  }).catch(() => {
    loading.value = false;
  });
}

function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

function resetQuery() {
  proxy.resetForm("queryRef");
  createTimeRange.value = [];
  queryParams.value.createStartTime = null;
  queryParams.value.createEndTime = null;
  handleQuery();
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length !== 1;
  multiple.value = selection.length === 0;
}

function handleExport() {
  proxy.download(
    'extra/mes/report/mes_report_chuXinHanJie/duanZi/export',
    { ...queryParams.value },
    `楚鑫端子焊接机_${new Date().getTime()}.xlsx`
  );
}


getList();
</script>

<style scoped>
/* 可根据需要添加样式 */
</style>