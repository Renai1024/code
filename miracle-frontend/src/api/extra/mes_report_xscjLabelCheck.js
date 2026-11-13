import request from '@/utils/request'

// 查询线束车间外观检测条码录入数据列表
export function listMes_report_xscjLabelCheck(query) {
  return request({
    url: '/extra/mes/report/mes_report_xscj/list',
    method: 'get',
    params: query
  })
}