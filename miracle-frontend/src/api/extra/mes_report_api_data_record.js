import request from '@/utils/request';

export function mes_report_api_data_record(query) {
  return request({
    url: '/extra/mes/report/mes_report_api_data_record/list',
    method: 'get',
    params: query
  });
}