import request from '@/utils/request';

export function mes_report_hengHeHongKaoXiang(query) {
  return request({
    url: '/extra/mes/report/mes_report_hengHeHongKaoXiang/list',
    method: 'get',
    params: query
  });
}