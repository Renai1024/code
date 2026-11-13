import request from '@/utils/request'

// 展示报表支持匿名访问，已有登录身份由通用请求处理器照常携带。
const root = '/extra/mes/report/mes_tyco_scan/packing/report'
export const listTycoPackingReport = params => request({ url: root + '/list', params })
export const getTycoPackingReport = id => request({ url: root + '/' + id })
