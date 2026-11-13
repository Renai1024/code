import request from '@/utils/request';

export function mes_report_zhongCeChargeMode(query) {
  return request({
    url: '/extra/mes/report/mes_report_zhongCeChargeMode/list',
    method: 'get',
    params: query
  });
}