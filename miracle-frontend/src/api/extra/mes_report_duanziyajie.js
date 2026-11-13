import request from '@/utils/request'

// 查询端子压接机数据列表
export function listMes_report_duanziyajie(query) {
  return request({
    url: '/extra/mes/report/mes_report_duanziyajie/list',
    method: 'get',
    params: query
  })
}