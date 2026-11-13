import request from '@/utils/request';

export function mes_report_pressStrippingIntegratedMachine(query) {
  return request({
    url: '/extra/mes/report/mes_report_pressStrippingIntegratedMachine/list',
    method: 'get',
    params: query
  });
}
