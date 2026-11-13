import request from '@/utils/request'

// 新增：新版装箱接口，操作幂等由operation+数据库收据保障，不用前端重复提交窗口代替。
const root = '/extra/mes/report/mes_tyco_scan/packing'
export const packingCurrent = kind => request({ url: root + '/current', params: { kind } })
export const packingList = params => request({ url: root + '/list', params })
export const packingDetail = id => request({ url: root + '/detail/' + id })
// 写入错误由装箱页面统一提示，避免通用提示与确认框重复。
export const packingOperate = (action, data) => request({ url: root + '/' + action, method: 'post', data, timeout: 30000, silentError: true, headers: { repeatSubmit: false } })
export const packingDraft = kind => request({ url: root + '/draft/' + kind })
export const packingDraftSave = (kind, data) => request({ url: root + '/draft/' + kind, method: 'put', data, headers: { repeatSubmit: false } })
