import request from '@/utils/request'

// 查询bydlabelprint列表
export function listBydlabelprint(query) {
  return request({
    url: '/extra/mes/report/bydlabelprint/list',
    method: 'get',
    params: query
  })
}

// 查询bydlabelprintscan列表(内连接)
export function listBydlabelprintscan(query) {
  return request({
    url: '/extra/mes/report/bydlabelprint/printScan/list',
    method: 'get',
    params: query
  })
}

// 查询未扫码标签记录
export function getMesBydLabelPrintUnscannedList(query) {
  return request({
    url: '/extra/mes/report/bydlabelprint/unscanned/list',
    method: 'get',
    params: query
  })
}

// 查询打印及扫描标签数量统计
export function getBydLabelPrintScanCount(query){
  return request({
    url: '/extra/mes/report/bydlabelprint/printScan/count',
    method: 'get',
    params: query
  })
}

// 查询bydlabelprint详细
export function getBydlabelprint(MaterialCode) {
  return request({
    url: '/extra/mes/report/bydlabelprint/' + MaterialCode,
    method: 'get'
  })
}

// 新增bydlabelprint
export function addBydlabelprint(data) {
  return request({
    url: '/extra/mes/report/bydlabelprint',
    method: 'post',
    data: data
  })
}

// 修改bydlabelprint
export function updateBydlabelprint(data) {
  return request({
    url: '/extra/mes/report/bydlabelprint',
    method: 'put',
    data: data
  })
}

// 删除bydlabelprint
export function delBydlabelprint(MaterialCode) {
  return request({
    url: '/extra/mes/report/bydlabelprint/' + MaterialCode,
    method: 'delete'
  })
}
