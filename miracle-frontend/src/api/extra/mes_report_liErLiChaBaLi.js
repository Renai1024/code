import request from '@/utils/request';

export function mes_report_liErLiChaBaLi(query) {
  return request({
    url: '/extra/mes/report/mes_report_liErLiChaBaLi/list',
    method: 'get',
    params: query
  });
}