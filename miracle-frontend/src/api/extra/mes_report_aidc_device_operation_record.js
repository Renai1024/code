import request from '@/utils/request';

export function mes_report_aidc_device_operation_record(query) {
  return request({
    url: '/extra/mes/report/mes_report_aidc_device_operation_record/list',
    method: 'get',
    params: query
  });
}