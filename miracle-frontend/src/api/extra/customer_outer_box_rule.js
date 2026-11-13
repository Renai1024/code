import request from '@/utils/request'

// 查询客户外箱标签解析规则列表
export function listOuterBoxRule(query) {
  return request({
    url: '/extra/customer/outerBoxRule/list',
    method: 'get',
    params: query
  })
}

// 查询所有启用的客户规则（下拉选择用）
export function getActiveRules() {
  return request({
    url: '/extra/customer/outerBoxRule/active',
    method: 'get'
  })
}

// 查询客户规则详细
export function getOuterBoxRule(id) {
  return request({
    url: '/extra/customer/outerBoxRule/' + id,
    method: 'get'
  })
}

// 新增客户规则
export function addOuterBoxRule(data) {
  return request({
    url: '/extra/customer/outerBoxRule',
    method: 'post',
    data: data
  })
}

// 修改客户规则
export function updateOuterBoxRule(data) {
  return request({
    url: '/extra/customer/outerBoxRule',
    method: 'put',
    data: data
  })
}

// 删除客户规则
export function delOuterBoxRule(ids) {
  return request({
    url: '/extra/customer/outerBoxRule/' + ids,
    method: 'delete'
  })
}
