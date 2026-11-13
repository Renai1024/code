import request from '@/utils/request'
// 新增：泰科扫码独立接口；箱码使用纯文本，避免控制字符被手动拼接JSON破坏。
export const parseTycoBox = box => request({ url: '/extra/mes/report/mes_tyco_scan/parse', method: 'post', data: box, headers: { 'Content-Type': 'text/plain;charset=UTF-8', repeatSubmit: false } })
// 新增：按最新要求独立校验产品码全局唯一，历史箱内校验已保留。
export const isTycoProductUsed = code => request({ url: '/extra/mes/report/mes_tyco_scan/used', params: { code } })
export const submitTycoScan = data => request({ url: '/extra/mes/report/mes_tyco_scan', method: 'post', data, timeout: 120000, headers: { repeatSubmit: false } })
export const listTycoScans = params => request({ url: '/extra/mes/report/mes_tyco_scan/list', params })
export const tycoScanProducts = id => request({ url: `/extra/mes/report/mes_tyco_scan/${id}/products` })
// 新增：账号由服务端登录身份确定，不接收客户端传入的用户ID；关闭自动重复提交拦截，由版本号保障并发。
export const getTycoDraft = () => request({ url: '/extra/mes/report/mes_tyco_scan/draft' })
export const saveTycoDraft = data => request({ url: '/extra/mes/report/mes_tyco_scan/draft', method: 'put', data, headers: { repeatSubmit: false } })
export const clearTycoDraft = data => request({ url: '/extra/mes/report/mes_tyco_scan/draft', method: 'delete', data, headers: { repeatSubmit: false } })

/** 新增：按项目现有删除接口习惯传入UUID数组，后台执行箱头与明细的事务逻辑删除。 */
export function delTycoScan(ids) {
  return request({
    url: '/extra/mes/report/mes_tyco_scan/' + ids.join(','),
    method: 'delete'
  })
}
