import request from '@/utils/request'

// 查询电测线数据列表
export function listMes_report_diancexian(query) {
  return request({
    url: '/extra/mes/report/mes_report_diancexian/list',
    method: 'get',
    params: query
  })
}