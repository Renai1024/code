import request from '@/utils/request'

// 楚鑫端子焊接机数据查询
export function mes_report_chuXinDuanZiHan(query) {
  return request({
    url: '/extra/mes/report/mes_report_chuXinHanJie/duanZi/list',
    method: 'get',
    params: query
  })
}