import request from '@/utils/request'

// 查询绝缘耐压测试仪数据列表
export function mes_report_breakovercheckth9110(query) {
  return request({
    url: '/extra/mes/report/mes_report_breakoverCheckTH9110/list',
    method: 'get',
    params: query
  })
}