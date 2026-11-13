import request from '@/utils/request'

// 查询万肯耐压（有码）测试数据列表
export function list_WKyouma_WKACCheck(query) {
  return request({
    url: '/extra/mes/report/wkaccheckH/wkyouma/list',
    method: 'get',
    params: query
  })
}

// 查询安费诺测试数据列表
export function list_AFN_WKACCheck(query) {
  return request({
    url: '/extra/mes/report/wkaccheckH/afn/list',
    method: 'get',
    params: query
  })
}

// 查询泰科电阻测试数据列表
export function list_TKdr_WKACCheck(query) {
  return request({
    url: '/extra/mes/report/wkaccheckH/tkdr/list',
    method: 'get',
    params: query
  })
}