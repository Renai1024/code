import request from '@/utils/request'

// ==================== 查询 ====================

/**
 * 根据工单号查询
 * @param {string} productionOrderNo - 生产工单号
 */
export function getProdOrder(productionOrderNo) {
    return request({
        url: '/api/prod-order/' + productionOrderNo,
        method: 'get'
    })
}


/**
 * 分页列表查询
 * @param {Object} query - 查询参数
 * @param {string} query.productionOrderNo - 工单号（模糊查询）
 * @param {string} query.materialCode - 物料编码（模糊查询）
 * @param {string} query.materialName - 物料名称（模糊查询）
 * @param {number} query.pageNum - 页码
 * @param {number} query.pageSize - 每页条数
 */
export function listProdOrder(query) {
    return request({
        url: '/api/prod-order-list/list',
        method: 'get',
        params: query
    })
}

// ==================== 新增 ====================

/**
 * 新增生产工单
 * @param {Object} data - 工单数据
 * @param {string} data.productionOrderNo - 工单号
 * @param {string} data.workshop - 车间
 * @param {string} data.materialCode - 物料编码
 * @param {string} data.materialName - 物料名称
 * @param {number} data.plannedQuantity - 计划数量
 * @param {string} data.createTime - 创建时间
 * @param {string} data.image - 图片URL
 */
export function addProdOrder(data) {
    return request({
        url: '/api/prod-order-list/add',
        method: 'post',
        data: data
    })
}

/**
 * 批量新增生产工单
 * @param {Array} data - 工单数据列表
 */
export function addBatchProdOrder(data) {
    return request({
        url: '/api/prod-order-list/addBatch',
        method: 'post',
        data: data
    })
}

// ==================== 修改 ====================

/**
 * 修改生产工单（只修改车间和计划数量）
 * @param {Object} data - 工单数据
 * @param {string} data.productionOrderNo - 工单号（必填）
 * @param {string} data.workshop - 车间
 * @param {number} data.plannedQuantity - 计划数量
 */
export function updateProdOrder(data) {
    return request({
        url: '/api/prod-order-list/edit',
        method: 'put',
        data: data
    })
}

// ==================== 删除 ====================

/**
 * 删除生产工单
 * @param {string} productionOrderNo - 工单号
 */
export function delProdOrder(productionOrderNo) {
    return request({
        url: '/api/prod-order-list/' + productionOrderNo,
        method: 'delete'
    })
}

/**
 * 批量删除生产工单
 * @param {Array} orderNoList - 工单号列表
 */
export function delBatchProdOrder(orderNoList) {
    return request({
        url: '/api/prod-order-list/deleteBatch',
        method: 'delete',
        data: orderNoList
    })
}

// ==================== 测试 ====================

/**
 * 测试接口连通性
 */
export function testProdOrder() {
    return request({
        url: '/api/prod-order-list/test',
        method: 'get'
    })
}