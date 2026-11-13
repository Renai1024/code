import request from '@/utils/request'

// 查询海思耐压测试数据列表
export function listMes_report_hsnycs(query) {
  return request({
    url: '/extra/mes/report/mes_report_mupai3/hsnycs/list',
    method: 'get',
    params: query
  })
}