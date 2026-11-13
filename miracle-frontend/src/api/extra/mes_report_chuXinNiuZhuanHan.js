import request from '@/utils/request'

// 楚鑫扭转焊数据查询
export function mes_report_chuXinNiuZhuanHan(query) {
  return request({
    url: '/extra/mes/report/mes_report_chuXinHanJie/niuZhuan/list',
    method: 'get',
    params: query
  })
}