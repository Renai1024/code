import request from '@/utils/request';

export function mes_report_zhongCeLaoHuaRecord(query) {
  return request({
    url: '/extra/mes/report/mes_report_zhongCeLaoHuaRecord/list',
    method: 'get',
    params: query
  });
}