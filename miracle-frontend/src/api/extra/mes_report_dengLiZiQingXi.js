import request from '@/utils/request';

export function mes_report_dengLiZiQingXi(query) {
  return request({
    url: '/extra/mes/report/mes_report_dengLiZiQingXi/list',
    method: 'get',
    params: query
  });
}