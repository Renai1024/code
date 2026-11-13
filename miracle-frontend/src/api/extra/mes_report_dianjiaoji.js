import request from '@/utils/request'

// 查询点胶机机数据列表
export function listMes_report_dinajiaoji(query) {
  return request({
    url: '/extra/mes/report/mes_report_dianjiaoji/list',
    method: 'get',
    params: query
  })
}