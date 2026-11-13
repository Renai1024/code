import request from '@/utils/request'

// 验证外箱标签码（反解析客户料号）
export function validateOuterBox(customerCode, outerBoxBarcode) {
  return request({
    url: '/extra/customer/deliveryLabelBind/validateOuterBox',
    method: 'get',
    params: { customerCode, outerBoxBarcode }
  })
}

// 验证出货标签条码（查询客户料号）
export function validateDeliveryLabel(customerCode, deliveryLabelBarcode) {
  return request({
    url: '/extra/customer/deliveryLabelBind/validateDeliveryLabel',
    method: 'get',
    params: { customerCode, deliveryLabelBarcode }
  })
}

// 绑定外箱标签与出货标签（写入 BarcodeBind 表，通过 barcodeType 区分）
export function bindDeliveryLabel(data) {
  return request({
    url: '/extra/customer/deliveryLabelBind/bind',
    method: 'post',
    data: data
  })
}

// 查询绑定记录列表（自动过滤为客户出货标签绑定类型）
export function listDeliveryLabelBind(query) {
  return request({
    url: '/extra/customer/deliveryLabelBind/list',
    method: 'get',
    params: query
  })
}

// 查询绑定记录详细
export function getDeliveryLabelBindInfo(bindUuid) {
  return request({
    url: '/extra/customer/deliveryLabelBind/info',
    method: 'get',
    params: { bindUuid }
  })
}

// 删除绑定记录（逻辑删除）
export function delDeliveryLabelBind(id) {
  return request({
    url: '/extra/customer/deliveryLabelBind/' + id,
    method: 'delete'
  })
}
