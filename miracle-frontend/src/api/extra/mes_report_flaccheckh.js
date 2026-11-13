import request from '@/utils/request'

// 查询法拉耐压测试数据列表
export function list_XMFL_FLACCheck(query) {
  return request({
    url: '/extra/mes/report/flaccheckH/xmfl/list',
    method: 'get',
    params: query
  })
}

// 查询东电化耐压测试数据列表
export function list_DDH_FLACCheck(query) {
  return request({
    url: '/extra/mes/report/flaccheckH/ddh/list',
    method: 'get',
    params: query
  })
}

//万肯无码绝缘耐压测试
export function list_WKwuma_FLACCheck(query) {
  return request({
    url: '/extra/mes/report/flaccheckH/wkwuma/list',
    method: 'get',
    params: query
  })
}

//万肯气密性测试
export function list_WK_CheckPrint(query) {
  return request({
    url: '/extra/mes/report/flaccheckH/wkCheckPrint/list',
    method: 'get',
    params: query
  })
}

// 万肯气密性CPK分析
export function calculateCpkByWorkOrder(query) {
  return request({
    url: '/extra/mes/report/flaccheckH/wkCheckPrint/cpk',
    method: 'get',
    params: query
  })
}