  <!-- 典帧端子压接机 --><template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="auto">
      <el-form-item label="测试时间范围" prop="testTimeRange">
        <el-date-picker
          v-model="testTimeRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="YYYY-MM-DD"
          @change="handleDateRangeChange"
          clearable
          :unlink-panels="true"
        />
      </el-form-item>
      <el-form-item label="产品编号" prop="productNo">
        <el-input
          v-model="queryParams.productNo"
          placeholder="请输入产品编号"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="工位" prop="workStation">
        <el-input
          v-model="queryParams.workStation"
          placeholder="请输入工位"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备名称" prop="deviceName">
        <el-input
          v-model="queryParams.deviceName"
          placeholder="请输入设备名称"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="测试结果" prop="result">
        <el-input
          v-model="queryParams.result"
          placeholder="请输入测试结果"
          clearable
          @keyup.enter="handleQuery"
        />
      </el-form-item>
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

    <el-table v-loading="loading" :data="duanZiYaJieList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" min-width="40" align="center" />
      <el-table-column label="生产批次号" align="center" prop="productNo" min-width="120" show-overflow-tooltip/>
      <el-table-column label="工站编号" align="center" prop="workStation" min-width="100" show-overflow-tooltip/>
      <el-table-column label="设备名称" align="center" prop="deviceName" min-width="120" show-overflow-tooltip/>
      <el-table-column label="测试时间" align="center" prop="dateTime" min-width="160" show-overflow-tooltip>
        <template #default="scope">
          <span>{{ parseTime(scope.row.dateTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="压力峰值" align="center" prop="maxKg" min-width="100" show-overflow-tooltip/>
      <el-table-column label="峰值判定结果" align="center" prop="maxKgDif" min-width="90" show-overflow-tooltip/>
      <el-table-column label="峰值公差上限" align="center" prop="maxKgMaxDif" min-width="90" show-overflow-tooltip/>
      <el-table-column label="峰值公差下限" align="center" prop="maxKgMinDif" min-width="90" show-overflow-tooltip/>
      <el-table-column label="波峰偏移判定结果" align="center" prop="maxKgPosDif" min-width="90" show-overflow-tooltip/>
      <el-table-column label="波峰偏移公差上限" align="center" prop="maxKgPosMaxDif" min-width="90" show-overflow-tooltip/>
      <el-table-column label="波峰偏移公差下限" align="center" prop="maxKgPosMinDif" min-width="90" show-overflow-tooltip/>
      <el-table-column label="面积判定结果" align="center" prop="totalAreaDif" min-width="90" show-overflow-tooltip/>
      <el-table-column label="面积公差上限" align="center" prop="totalAreaMaxDif" min-width="90" show-overflow-tooltip/>
      <el-table-column label="面积公差上限" align="center" prop="totalAreaMinDif" min-width="90" show-overflow-tooltip/>
      <el-table-column label="少芯线判定结果" align="center" prop="area3Dif" min-width="90" show-overflow-tooltip/>
      <el-table-column label="少芯线公差上限" align="center" prop="area3MaxDif" min-width="90" show-overflow-tooltip/>
      <el-table-column label="少芯线公差下限" align="center" prop="area3MinDif" min-width="90" show-overflow-tooltip/>
      <el-table-column label="CPK" align="center" prop="cpk" min-width="80" show-overflow-tooltip/>
      <el-table-column label="安定性" align="center" prop="stability" min-width="90" show-overflow-tooltip/>
      <el-table-column label="压接高度" align="center" prop="pressHigh" min-width="80" show-overflow-tooltip/>
      <el-table-column label="线材型号" align="center" prop="lineType" min-width="100" show-overflow-tooltip/>
      <el-table-column label="端子型号" align="center" prop="termType" min-width="100" show-overflow-tooltip/>
      <el-table-column label="模具型号" align="center" prop="modeType" min-width="100" show-overflow-tooltip/>
      <el-table-column label="检测结果" align="center" prop="result" min-width="100" show-overflow-tooltip/>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      v-model:page="queryParams.pageNum"
      v-model:limit="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script setup name="MesDZDuanZiYaJieJi">
import { ref, reactive, toRefs } from "vue";
import { getCurrentInstance } from "vue";
import { listMes_report_dzduanziyajieji } from "@/api/extra/mes_report_dzduanziyajieji";

const { proxy } = getCurrentInstance();

const duanZiYaJieList = ref([]);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const testTimeRange = ref([]);

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    productNo: null,
    workStation: null,
    deviceName: null,
    result: null,
    testStartTime: null,
    testEndTime: null
  }
});

const { queryParams } = toRefs(data);

// 日期范围变化处理
function handleDateRangeChange(range) {
  if (range && range.length === 2) {
    queryParams.value.testStartTime = range[0];
    queryParams.value.testEndTime = range[1];
  } else {
    queryParams.value.testStartTime = null;
    queryParams.value.testEndTime = null;
  }
}

/** 查询端子压接数据列表 */
function getList() {
  loading.value = true;
  listMes_report_dzduanziyajieji(queryParams.value).then(response => {
    duanZiYaJieList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  }).catch(() => {
    loading.value = false;
  });
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  testTimeRange.value = [];
  queryParams.value.testStartTime = null;
  queryParams.value.testEndTime = null;
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id);
  single.value = selection.length !== 1;
  multiple.value = selection.length === 0;
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download(
    'extra/mes/report/mes_report_dzduanziyajieji/export',
    { ...queryParams.value },
    `典帧端子压接机_${new Date().getTime()}.xlsx`
  );
}

// 初始化加载
getList();
</script>