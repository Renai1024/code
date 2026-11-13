import request from '@/utils/request'

// 查询典帧端子压接机数据列表
export function listMes_report_dzduanziyajieji(query) {
  return request({
    url: '/extra/mes/report/mes_report_dzduanziyajieji/list',
    method: 'get',
    params: query
  })
}