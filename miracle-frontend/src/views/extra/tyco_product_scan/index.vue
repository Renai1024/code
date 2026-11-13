<template>
  <!-- 新增：双页签默认内箱，v-show保留两页任务；旧页面完整保存在implementation-history/20261103-tyco-inner-outer/index.vue。 -->
  <div class="app-container tyco-packing-page">
    <!-- 历史扫码页报表入口保留，当前统一使用系统菜单访问。
    <div v-hasPermi="['extra:mes_tyco_scan:list']" style="margin-bottom: 12px">
      <router-link to="/report/mes/tyco_product_scan" target="_blank"><el-button>装箱追溯报表</el-button></router-link>
    </div>
    -->
    <el-tabs v-model="active">
      <el-tab-pane label="内箱装箱" name="INNER"><PackingPanel kind="INNER" :active="active==='INNER'" /></el-tab-pane>
      <el-tab-pane label="外箱装箱" name="OUTER"><PackingPanel kind="OUTER" :active="active==='OUTER'" /></el-tab-pane>
    </el-tabs>
  </div>
</template>
<script setup name="TycoProductScan">
import { ref } from 'vue'
import PackingPanel from './PackingPanel.vue'
// 新增：每次打开页面默认内箱，切换不触发取消或清除草稿。
const active=ref('INNER')
</script>
<style scoped>
/* 新增：沿用项目容器与PDA边距。 */
@media(max-width:768px){.tyco-packing-page{padding:10px}}
</style>
