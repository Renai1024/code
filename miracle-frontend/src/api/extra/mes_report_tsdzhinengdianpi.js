import request from '@/utils/request'

// 查询TSD智能电批数据列表
export function listTSDZhiNengDianPi(query) {
  return request({
    url: '/extra/mes/report/mes_report_tsdzhinengdianpi/list',
    method: 'get',
    params: query
  })
}