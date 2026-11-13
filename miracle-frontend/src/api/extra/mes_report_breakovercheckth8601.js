import request from '@/utils/request'

// 查询精密综合测试仪数据列表
export function mes_report_breakovercheckth8601(query) {
  return request({
    url: '/extra/mes/report/mes_report_breakoverCheckTH8601/list',
    method: 'get',
    params: query
  })
}