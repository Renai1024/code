import request from '@/utils/request'

export function getProductList() {
  return request({
    url: '/extra/barcode/bind/products',
    method: 'get'
  })
}

export function validateBarcode(params) {
  return request({
    url: '/extra/barcode/bind/validate',
    method: 'get',
    params: params
  })
}

export function bindBarcode(data) {
  return request({
    url: '/extra/barcode/bind/bind',
    method: 'post',
    data: data
  })
}

export function listBarcodeBind(query) {
  return request({
    url: '/extra/barcode/bind/list',
    method: 'get',
    params: query
  })
}

export function getBarcodeBindInfo(bindUuid) {
  return request({
    url: '/extra/barcode/bind/info',
    method: 'get',
    params: { bindUuid }
  })
}

export function delBarcodeBind(id) {
  return request({
    url: '/extra/barcode/bind/' + id,
    method: 'delete'
  })
}