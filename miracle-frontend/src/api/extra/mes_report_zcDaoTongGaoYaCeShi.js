//中测充电枪导通高压测试报表

import request from '@/utils/request'

// 查询中测充电枪导通高压测试数据列表
export function listMes_report_zcDaoTongGaoYaCeShi(query) {
  return request({
    url: '/extra/mes/report/mes_report_zcDaoTongGaoYaCeShi/list',
    method: 'get',
    params: query
  })
}