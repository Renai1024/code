import request from '@/utils/request';

export function mes_report_zhanShengMangChaJi(query) {
  return request({
    url: '/extra/mes/report/mes_report_zhanShengMangChaJi/list',
    method: 'get',
    params: query
  });
}