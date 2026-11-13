import request from '@/utils/request'

// 查询线材导通数据列表
export function listMes_report_shebeizu(query) {
  return request({
    url: '/extra/mes/report/mes_report_shebeizu/list',
    method: 'get',
    params: query
  })
}