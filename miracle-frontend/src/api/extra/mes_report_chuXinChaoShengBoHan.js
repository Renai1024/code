import request from '@/utils/request'

// 楚鑫超声波焊接机数据查询
export function mes_report_chuXinChaoShengBoHan(query) {
  return request({
    url: '/extra/mes/report/mes_report_chuXinHanJie/chaoShengBo/list',
    method: 'get',
    params: query
  })
}