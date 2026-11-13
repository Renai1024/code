<template>
  <!-- 新增：每个页签独立组件和草稿，切换不销毁任务；扫码状态以服务器返回为准。 -->
  <div class="packing-panel">
    <div class="packing-toolbar">
      <el-button :disabled="busy" icon="Refresh" @click="load()">刷新/恢复任务</el-button>
      <el-button v-if="conflict" :disabled="busy || !!pending" @click="useRemote">读取服务器草稿</el-button>
    </div>
    <!-- 调试用同步状态不再展示，原模板保留供排查时参考。
    <p class="packing-hint">{{ syncText }}</p>
    -->
    <el-form label-width="85px" @submit.prevent v-hasPermi="['extra:mes_tyco_scan:add']">
      <el-form-item :label="inner ? '内箱码' : '外箱码'">
        <el-input ref="boxRef" v-model="input.boxInput" :disabled="blocked || !!task" inputmode="text"
                  :placeholder="inner ? '料号-防呆码-流水-QTY:数量' : '扫描客户外箱二维码'" @input="edited" @keyup.enter="start" />
      </el-form-item>
      <el-button v-if="!task" type="primary" :disabled="blocked || !input.boxInput" @click="start">确认{{ inner ? '内' : '外' }}箱码</el-button>
      <template v-if="task">
        <el-descriptions :column="1" border class="packing-info">
          <el-descriptions-item label="客户料号">{{ task.part }}</el-descriptions-item>
          <!-- 防呆码随当前内箱任务从后端返回，恢复任务后也可核对。 -->
          <el-descriptions-item v-if="inner && task.snPrefix" label="防呆码">{{ task.snPrefix }}</el-descriptions-item>
          <el-descriptions-item :label="inner ? '内箱标识' : '物料追溯码'">{{ task.identity }}</el-descriptions-item>
          <el-descriptions-item :label="inner ? '标签数量' : '装箱数量'">{{ task.quantity }}</el-descriptions-item>
          <template v-if="!inner">
            <el-descriptions-item label="版本">{{ task.revision }}</el-descriptions-item>
            <el-descriptions-item label="销售订单">{{ task.salesOrder }}</el-descriptions-item>
            <el-descriptions-item label="生产批次">{{ task.productionBatch }}</el-descriptions-item>
            <el-descriptions-item label="箱序/日期码">{{ task.boxSequence }} / {{ task.dateCode }}</el-descriptions-item>
          </template>
        </el-descriptions>
        <div v-if="inner" class="packing-counts">应扫 {{ task.quantity }}　已扫 {{ products.length }}　剩余 {{ Math.max(0,task.quantity-products.length) }}</div>
        <el-form-item :label="inner ? '产品SN码' : '内箱码'">
          <el-input ref="scanRef" v-model="input.scanInput" :disabled="blocked || (inner && products.length >= task.quantity)" inputmode="text"
                    :placeholder="inner ? '扫描产品SN码' : '扫描已完成装箱的内箱码'" @input="edited" @keyup.enter="scan" />
        </el-form-item>
        <!-- 尾箱开关与确认 SN 同排紧凑显示，原整行布局保存在历史快照。 -->
        <div class="packing-scan-actions">
          <el-button :disabled="blocked || !input.scanInput || (inner && products.length >= task.quantity)" @click="scan">{{ inner ? '确认产品SN' : '校验并关联内箱' }}</el-button>
          <el-checkbox v-if="inner" v-model="input.tailBox" :disabled="blocked" border class="packing-tail-option" @change="edited">
            是否尾箱
          </el-checkbox>
        </div>
        <template v-if="inner">
          <div class="packing-products"><div v-for="(code,index) in shown" :key="code" class="packing-product">
            <span>{{ index+1 }}. {{ code }}</span><el-button link type="danger" :disabled="blocked" @click="remove(code)">移除</el-button>
          </div></div>
          <el-button v-if="products.length>1" link type="primary" @click="expanded=!expanded">{{ expanded ? '收起' : '展开全部（共'+products.length+'条）' }}</el-button>
        </template>
        <div class="packing-actions">
          <el-button :disabled="blocked" @click="cancel">取消本次{{ inner ? '装箱' : '关联' }}</el-button>
          <el-button v-if="inner" type="primary" :disabled="blocked || !canFinish || !!input.scanInput" @click="openReview">完成装箱</el-button>
        </div>
<!--        <p class="packing-hint">未完成任务有效至 {{ parseTime(task.expiresAt) }}；读取和切换页签不会续期。</p>-->
      </template>
    </el-form>

    <!-- 优化：核对窗口保留只读明细，点击确认装箱即表示核对；不再要求额外勾选。请求仍绑定核对版本。 -->
    <el-dialog v-model="reviewOpen" title="装箱核对" width="min(600px, calc(100vw - 16px))" :close-on-click-modal="false" :close-on-press-escape="false" :show-close="!busy" class="packing-review" append-to-body>
      <template v-if="task">
        <p class="packing-code">内箱码：{{ task.rawCode }}</p><p>客户料号：<strong>{{ task.part }}</strong></p>
        <p>标签数量：{{ task.quantity }}　已扫SN：{{ products.length }}　剩余：{{ task.quantity-products.length }}</p>
        <p>是否尾箱：{{ input.tailBox ? '是' : '否' }}　实际装箱数量：{{ products.length }}</p>
        <div class="packing-products"><p v-for="code in products" :key="code">{{ code }}</p></div>
        <!-- 历史勾选确认保留：<el-checkbox v-model="reviewed" :disabled="busy">我已核对实物、客户料号及装箱数量，确认一致</el-checkbox> -->
      </template>
      <template #footer><el-button :disabled="busy" @click="reviewOpen=false">返回检查</el-button>
        <!-- 历史按钮禁用条件：!reviewed || busy；现以主动点击按钮确认，防止忙碌时重复提交。 -->
        <el-button type="primary" :loading="busy" :disabled="busy" @click="finish">确认装箱</el-button></template>
    </el-dialog>

    <el-divider>{{ inner ? '内箱装箱记录' : '外箱装箱记录' }}</el-divider>
    <!-- 批量拆箱仅在内箱页签开放，只选择本页已装箱且可拆箱的记录。 -->
    <div v-if="inner && canRelease" class="packing-toolbar">
      <el-button type="danger" plain :disabled="blocked || loading || !!task || !selectedInnerBoxes.length" @click="unpackSelected">
        批量拆箱<span v-if="selectedInnerBoxes.length">（{{ selectedInnerBoxes.length }}）</span>
      </el-button>
    </div>
    <el-table v-loading="loading" :data="rows" row-key="id" border empty-text="暂无有效装箱记录" @selection-change="selectedInnerBoxes = $event">
      <el-table-column v-if="inner && canRelease" type="selection" width="48" :selectable="canSelectForUnpack" />
      <el-table-column prop="identity" :label="inner ? '内箱标识' : '物料追溯码'" min-width="190" show-overflow-tooltip />
      <el-table-column prop="part" label="客户料号" min-width="130" />
      <el-table-column prop="quantity" label="数量" width="75" />
      <el-table-column v-if="inner" label="实际数量" width="90"><template #default="{row}">{{ row.packedQuantity ?? (['PACKED','BOUND'].includes(row.status) ? row.quantity : '—') }}</template></el-table-column>
      <el-table-column v-if="inner" label="尾箱" width="70"><template #default="{row}">{{ row.tailBox ? '是' : '否' }}</template></el-table-column>
      <el-table-column label="状态" width="120"><template #default="{row}">{{ statusText(row.status) }}</template></el-table-column>
      <el-table-column prop="ownerName" label="操作人" width="100" />
      <el-table-column label="操作" width="160"><template #default="{row}">
        <el-button link type="primary" @click="detail(row)">详情</el-button>
        <el-button v-if="(inner && row.status==='PACKED') || (!inner && row.status==='BOUND')" v-hasPermi="['extra:mes_tyco_scan:remove']"
                   link type="danger" :disabled="busy" @click="release(row)">{{ inner ? '拆箱' : '解绑' }}</el-button>
      </template></el-table-column>
    </el-table>
    <!-- 后端在分页前过滤无效记录，总数和当前页使用相同范围。 -->
    <pagination v-show="total>0" :total="total" v-model:page="query.pageNum" v-model:limit="query.pageSize" @pagination="list" />
    <el-dialog v-model="detailOpen" title="装箱记录详情" width="min(650px, calc(100vw - 16px))" append-to-body>
      <template v-if="detailRow"><p class="packing-code">{{ detailRow.rawCode }}</p><p>客户料号：{{ detailRow.part }}　数量：{{ detailRow.quantity }}</p>
        <p v-if="inner">是否尾箱：{{ detailRow.tailBox ? '是' : '否' }}　实际装箱数量：{{ detailRow.packedQuantity ?? (['PACKED','BOUND'].includes(detailRow.status) ? detailRow.quantity : '—') }}</p>
        <p>状态：{{ statusText(detailRow.status) }}　核对人：{{ detailRow.reviewedBy || '—' }}</p>
        <p v-if="detailRow.reviewedAt">核对时间：{{ parseTime(detailRow.reviewedAt) }}</p>
        <p v-if="detailRow.boundInnerCode" class="packing-code">关联内箱：{{ detailRow.boundInnerCode }}</p>
        <div class="packing-products"><p v-for="code in detailRow.products" :key="code">{{ code }}</p></div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted, onBeforeUnmount, nextTick, getCurrentInstance } from 'vue'
import { ElMessageBox } from 'element-plus'
import useUserStore from '@/store/modules/user'
import { packingCurrent, packingList, packingDetail, packingOperate, packingDraft, packingDraftSave } from '@/api/extra/tyco_packing'
import { CloudDraft } from './cloud-draft'

// 新增：任务/产品明细仅取数据库，Redis与localStorage只保护未确认输入和未决请求。
const props=defineProps({ kind:{type:String,required:true},active:Boolean })
const {proxy}=getCurrentInstance(), user=useUserStore()
const canScan=computed(()=>user.permissions.includes('*:*:*') || user.permissions.includes('extra:mes_tyco_scan:add'))
// 批量操作沿用单箱拆箱权限，包含没有扫码新增权限的拆箱人员。
const canRelease=computed(()=>user.permissions.includes('*:*:*') || user.permissions.includes('extra:mes_tyco_scan:remove'))
const selectedInnerBoxes=ref([])
const inner=computed(()=>props.kind==='INNER'), task=ref(null), input=reactive({boxInput:'',scanInput:'',tailBox:false})
const busy=ref(false),loading=ref(false),ready=ref(false),syncText=ref(''),conflict=ref(false),pending=ref(null)
// 历史状态：reviewed=ref(false)；不再维护勾选状态，确认动作由按钮承担。
const expanded=ref(false), reviewOpen=ref(false),reviewVersion=ref(-1)
const rows=ref([]),total=ref(0),query=ref({pageNum:1,pageSize:10}),detailOpen=ref(false),detailRow=ref(null)
let listRequest=0
const boxRef=ref(),scanRef=ref()
const products=computed(()=>task.value?.products || [])
// 尾箱允许 1 至标签数量，普通内箱必须扫满。
const canFinish=computed(()=>products.value.length>0 && products.value.length<=task.value?.quantity && (input.tailBox || products.value.length===task.value?.quantity))
const shown=computed(()=>expanded.value?products.value:products.value.slice(0,1))
const blocked=computed(()=>busy.value || !ready.value || conflict.value || !!pending.value)
const cacheError='本机存储不可用，装箱操作未提交'
let cloud,dirty=false,serial=0,timer,alive=true,loaded=false,localExpires=0
const uuid=()=>globalThis.crypto?.randomUUID?.() || 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g,c=>{const r=Math.random()*16|0;return(c==='x'?r:(r&3|8)).toString(16)})
const key=()=>`tyco-packing:v1:${user.id}:${props.kind}`
const snapshot=()=>({boxInput:input.boxInput,scanInput:input.scanInput,tailBox:input.tailBox,sessionId:task.value?.id || ''})
function cache() {
  // 新增：成功请求的收据仍可重试；组件卸载后不覆盖重新打开页面写入的本机缓存。
  if(!alive)return false
  try {
    localStorage.setItem(key(),JSON.stringify({expiresAt:localExpires || Date.now()+86400000,input:snapshot(),task:task.value,meta:cloud?.metadata(),dirty,pending:pending.value}))
    return true
  } catch {return false}
}
function edited() {
  dirty=true;serial++;localExpires=Date.now()+86400000;cache();clearTimeout(timer);timer=setTimeout(()=>sync(),200)
}
async function sync(showCacheError=false) {
  clearTimeout(timer)
  if(!cloud || conflict.value)return false
  const seq=serial
  try {
    syncText.value='正在同步输入草稿…'
    await cloud.save(snapshot(),()=>{if(!cache())throw Error(cacheError)},result=>{dirty=seq!==serial;if(!dirty)localExpires=result.expiresAt;cache()})
    syncText.value=dirty?'有新输入待同步':'输入已同步；已接收SN保存在服务器数据库'
    return !dirty
  } catch(e) {
    if(String(e.message).includes('草稿版本冲突'))conflict.value=true
    // 草稿接口已有请求提示，本机存储失败只在用户提交时提示。
    if(showCacheError && e.message===cacheError)proxy.$modal.msgError(cacheError)
    return false
  }
}
async function useRemote() {
  if(pending.value)return
  try { await proxy.$modal.confirm('读取服务器草稿？本机内容会另存备份，不自动合并。');localStorage.setItem(key()+':backup',localStorage.getItem(key()) || '') }
  catch{return}
  await load(true)
}
async function load(force=false) {
  if(busy.value)return
  busy.value=true;ready.value=false
  try {
    let saved
    try { saved=JSON.parse(localStorage.getItem(key()) || 'null');if(saved?.expiresAt<=Date.now())saved=null } catch {saved=null}
    // 页面恢复时保留同一个操作号和版本，统一通过当前入口核对未确认结果。
    const savedCommand=pending.value || saved?.pending || null
    if(!canScan.value){
      // 仅有拆箱权限的用户也可恢复自己的解绑、拆箱和批量拆箱操作。
      if(canRelease.value && ['unbind','unpack','unpack-batch'].includes(savedCommand?.action)){
        pending.value=savedCommand
        await transmit()
      } else await list()
      ready.value=true;loaded=true;return
    }
    const [db,remote]=await Promise.all([packingCurrent(props.kind),packingDraft(props.kind)])
    if(!alive)return
    conflict.value=false
    task.value=db.data;pending.value=savedCommand
    // 新增：未决数据库请求必须保留原operation和版本，不能因刷新生成新操作。
    if(pending.value && !task.value)task.value=saved?.task || null
    const meta=saved?.meta, localDirty=!force && (saved?.dirty || meta?.pending)
    cloud=new CloudDraft({save:d=>packingDraftSave(props.kind,d)},uuid,localDirty?meta:{version:remote.data.version})
    let values=localDirty?saved.input:remote.data.data
    if(localDirty && (meta?.version || '')!==remote.data.version && meta?.pending?.operation!==remote.data.operation) {
      conflict.value=true
    }
    if(values?.sessionId!==(task.value?.id || ''))values=null
    input.boxInput=values?.boxInput || '';input.scanInput=values?.scanInput || ''
    input.tailBox=values?.tailBox===true
    dirty=!!localDirty;expanded.value=false;reviewOpen.value=false;ready.value=true;loaded=true
    // 等待核对时沿用本机操作的有效期，避免用草稿期限覆盖未确认请求的缓存期限。
    localExpires=(savedCommand && saved?.expiresAt) || (localDirty?saved.expiresAt:remote.data.expiresAt)
    cache();syncText.value='已恢复服务器任务，读取不会延长任务有效期'
    if(pending.value){await transmit();return}
    if(dirty && !conflict.value)await sync()
    await list()
  } catch {
    // 读取失败由通用请求层提示，保留本机记录供下次恢复。
  }
  finally {busy.value=false;focus()}
}
async function focus() {await nextTick();if(props.active&&!busy.value&&!pending.value)(task.value?scanRef:boxRef).value?.input?.focus({preventScroll:true})}
async function errorDialog(text) {
  try {await ElMessageBox.alert(text,'扫码校验失败',{type:'error',confirmButtonText:'确定',showClose:false,closeOnClickModal:false,closeOnPressEscape:false})}catch{}
}
async function send(action,extra={}) {
  if(blocked.value)return
  busy.value=true
  try {
    // 历史拆箱和解绑独立于扫码输入草稿，无需同步草稿才能提交。
    if(action!=='unbind' && action!=='unpack' && action!=='unpack-batch' && !await sync(true))return
    pending.value={action,data:{operation:uuid(),sessionId:task.value?.id || null,kind:props.kind,version:task.value?.version ?? null,...extra}}
    localExpires=Date.now()+86400000
    if(!cache()){pending.value=null;proxy.$modal.msgError(cacheError);return}
    await transmit()
  } finally {busy.value=false;focus()}
}
async function transmit() {
  const command=pending.value
  try {
    const result=await packingOperate(command.action,command.data)
    if(!alive)return
    pending.value=null
    if(command.action==='unpack-batch'){
      // 刷新后拆箱记录随列表过滤消失，回到首页避免停留在已经空掉的末页。
      query.value.pageNum=1
      cache()
      await list()
      proxy.$modal.msgSuccess(`已拆箱 ${command.data.targets.length} 个内箱`)
      return
    }
    const resultTask=result.data
    task.value=resultTask?.status==='SCANNING'?resultTask:null
    if(!task.value)input.tailBox=false
    input.boxInput='';input.scanInput='';expanded.value=false;reviewOpen.value=false
    serial++;dirty=true;cache();await sync();await list()
    if(command.action==='finish')proxy.$modal.msgSuccess('内箱装箱完成')
    if(command.action==='bind')proxy.$modal.msgSuccess('内外箱关联完成')
  } catch(e) {
    if(!alive)return
    // 新增：只有明确业务拒绝才释放未决标识；网络/未知错误原样保留，禁止取消绕过核对。
    const text=String(e.message || '')
    // 兼容新旧防呆报错文案，两种均属明确拒绝，只展示一次确认框。
    const definite=e.businessCode===403 || /^(内箱|外箱|产品SN|产品码|扫描的SN防呆码错误|扫描SN码错误|扫描内箱|同一箱|此页签|此箱码|任务已|当前任务|装箱任务|装箱类型|请先|请填写|关联状态|操作编号|操作或|操作类型|请求|装箱数量|箱码为空|当前账号|装箱操作|记录不存在)/.test(text)
    reviewOpen.value=false
    if(definite){
      pending.value=null
      if(command.action==='unpack-batch'){
        // 整批被拒绝时刷新版本和状态，保留本页的扫码输入草稿。
        cache()
        await errorDialog(text)
        await list()
        return
      }
      if(command.action==='start')input.boxInput=''
      else input.scanInput=''
      serial++;dirty=true;cache()
      await errorDialog(text)
      if(text.includes('任务已') || text.includes('当前任务')){
        task.value=(await packingCurrent(props.kind)).data
      }
      await sync()
    } else {
      // 未确认请求留在本机，重新联网或刷新时继续恢复。
      cache()
    }
  }
}
async function start(){if(input.boxInput)await send('start',{code:input.boxInput})}
async function scan(){if(input.scanInput)await send(inner.value?'scan':'bind',{code:input.scanInput})}
async function remove(code){await send('remove',{code})}
function openReview(){reviewVersion.value=task.value.version;reviewOpen.value=true}
// 优化：原 reviewed.value 勾选条件保存在历史快照；仅允许从当前打开的核对窗口确认，版本变化时拒绝提交。
async function finish(){if(reviewOpen.value && reviewVersion.value===task.value?.version)await send('finish',{reviewed:true,tailBox:input.tailBox})}
async function cancel(){try{await proxy.$modal.confirm('取消当前任务并释放本次占用？')}catch{return}await send('cancel')}
async function list() {
  const request = ++listRequest
  loading.value = true
  try {
    const result = await packingList({ ...query.value, kind: props.kind })
    if (request !== listRequest) return
    rows.value = result.rows
    total.value = result.total
  } catch {
    // 请求层已提示，保留上次读取的列表。
  } finally {
    if (request === listRequest) loading.value = false
  }
}
async function detail(row) {
  const result = await packingDetail(row.id)
  // 列表加载后该箱可能已失效，详情接口此时返回空数据。
  if (!result.data) {
    await list()
    return
  }
  detailRow.value = result.data
  detailOpen.value = true
}
// 只允许勾选已完成装箱的内箱，后端提交时还会检查关联状态和版本。
function canSelectForUnpack(row) {
  return row.status==='PACKED' && !blocked.value && !task.value && !loading.value
}
async function unpackSelected() {
  if(blocked.value || task.value || loading.value || !inner.value || !canRelease.value)return
  const targets=selectedInnerBoxes.value.map(row=>({sessionId:row.id,version:row.version}))
  if(!targets.length || targets.length>100){
    proxy.$modal.msgWarning('每次请选择 1 至 100 个内箱')
    return
  }
  // 固定确认时选中的 ID 和版本，网络异常时可按原批次安全重试。
  try{await proxy.$modal.confirm(`确认拆箱选中的 ${targets.length} 个内箱？这会释放其中的全部产品 SN。`)}catch{return}
  await send('unpack-batch',{targets})
}
async function release(row){
  if(busy.value || pending.value)return
  if(task.value){proxy.$modal.msgWarning('请先完成或取消本页未完成任务，再操作历史记录');return}
  // 优化：拆箱仍需确认释放 SN，但不输入原因；旧版共用原因输入逻辑保存在历史快照。
  const target={sessionId:row.id,version:row.version}
  if(inner.value){
    try{await proxy.$modal.confirm('拆箱会释放全部产品 SN，确认拆箱吗？')}catch{return}
    await send('unpack',target)
    return
  }
  // 原 outer.value 分支已保存到历史快照。内箱分支已返回，此处为外箱解绑。
  try {
    await proxy.$modal.confirm('解绑将解除内外箱关联，确认解绑吗？')
  } catch {return}
  await send('unbind', target)
}
function statusText(value){return({SCANNING:'扫描中',PACKED:'内箱已装箱',BOUND:'已关联外箱',UNBOUND:'已解绑/拆箱',CANCELLED:'已取消',EXPIRED:'已过期'})[value] || value}
// 新增：同浏览器其他标签修改输入时停止自动覆盖；跨设备由Redis版本和SQL任务版本双重检查。
function storageChanged(event){if(event.key===key())conflict.value=true}
watch(() => props.active, active => {
  if (!active) return
  if (!loaded || pending.value) {
    load()
    return
  }
  // 切回页签时只刷新记录列表，保留正在输入的条码和草稿。
  list()
  focus()
})
// 浏览器重新联网时自动恢复当前页签的未确认请求。
function recoverOnReconnect(){if(props.active && (!loaded || pending.value))load()}
onMounted(()=>{
  window.addEventListener('storage',storageChanged)
  window.addEventListener('online',recoverOnReconnect)
  if(props.active)load()
})
onBeforeUnmount(()=>{
  alive=false;clearTimeout(timer)
  window.removeEventListener('storage',storageChanged)
  window.removeEventListener('online',recoverOnReconnect)
})
</script>

<style scoped>
/* 新增：PDA页面内直接扫码，label/input同排，记录横向滚动，操作区保持可触达。 */
.packing-toolbar,.packing-actions{display:flex;gap:8px;flex-wrap:wrap;margin:12px 0}
.packing-actions{justify-content:center}.packing-info{margin:14px 0}.packing-counts{font-size:18px;margin:16px 0}
.packing-hint{font-size:13px;color:var(--el-text-color-secondary);overflow-wrap:anywhere}
.packing-products{max-height:28vh;overflow:auto;margin:12px 0}.packing-product{display:flex;justify-content:space-between;align-items:center;gap:10px;min-height:40px;overflow-wrap:anywhere}
.packing-code{overflow-wrap:anywhere}
/* 控件按内容宽度同排展示，PDA 下两者共同扩大点击高度。 */
.packing-scan-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}
.packing-tail-option {
  flex: none;
  width: auto;
  height: 32px;
  margin: 0;
  padding: 0 10px;
  white-space: nowrap;
  box-sizing: border-box;
}
@media (max-width: 768px) {
  .packing-tail-option,
  .packing-scan-actions :deep(.el-button) {
    min-height: 44px;
  }
}
.packing-info :deep(.el-descriptions__table){table-layout:fixed}.packing-info :deep(.el-descriptions__content){overflow-wrap:anywhere}
@media(max-width:768px){.packing-panel :deep(.el-input__wrapper){min-height:44px}.packing-panel :deep(.el-button){min-height:40px}.packing-actions .el-button{min-height:44px}.packing-panel :deep(.el-form-item__label){padding-right:8px}}
</style>
<style>
/* 新增：核对弹框传送到body，独立类控制PDA高度与长确认文字换行。 */
.packing-review .el-checkbox{height:auto;white-space:normal}.packing-review .el-checkbox__label{white-space:normal}
@media(max-width:768px){.packing-review{margin-top:3vh;max-height:92vh;overflow:auto}.packing-review .el-dialog__footer .el-button{min-height:44px}}
</style>
