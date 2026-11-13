import request from '@/utils/request'

// 查询外箱绑定记录列表
export function listOuterBoxBind(query) {
  return request({
    url: '/extra/mes/outerBoxBind/list',
    method: 'get',
    params: query
  })
}

// 根据外箱编号查询绑定的产品编号列表
export function getProductsByOuterBoxBarcode(outerBoxBarcode) {
  return request({
    url: '/extra/mes/outerBoxBind/products',
    method: 'get',
    params: {
      outerBoxBarcode: outerBoxBarcode
    }
  })
}

// 查询外箱绑定记录分组列表
export function groupListOuterBoxBind(query) {
  return request({
    url: '/extra/mes/outerBoxBind/groupList',
    method: 'get',
    params: query
  })
}

// 查询外箱绑定记录详细
export function getOuterBoxBind(outerBoxBarcode) {
  return request({
    url: '/extra/mes/outerBoxBind/info',
    method: 'get',
    params: {
      outerBoxBarcode: outerBoxBarcode
    }
  })
}

// 验证外箱编码
export function validateOuterBox(outerBoxBarcode) {
  return request({
    url: '/extra/mes/outerBoxBind/validateOuterBox',
    method: 'get',
    params: { outerBoxBarcode }
  })
}

// 验证产品编码
export function validateProduct(productBarcode) {
  return request({
    url: '/extra/mes/outerBoxBind/validateProduct',
    method: 'get',
    params: { productBarcode }
  })
}

// 外箱编码绑定产品编码
export function bindOuterBox(data) {
  return request({
    url: '/extra/mes/outerBoxBind/bind',
    method: 'post',
    data: data
  })
}

// 删除外箱绑定记录
export function delOuterBoxBind(outerBoxBarcode) {
  return request({
    url: '/extra/mes/outerBoxBind/delete',
    method: 'post',
    params: { outerBoxBarcode }
  })
}
