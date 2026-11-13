//立维斯气密性报表

import request from '@/utils/request'

// 查询立维斯气密性数据列表
export function listMes_report_lwsQiMiXing(query) {
  return request({
    url: '/extra/mes/report/mes_report_lwsQiMiXing/list',
    method: 'get',
    params: query
  })
}