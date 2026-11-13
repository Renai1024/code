import request from '@/utils/request'

/**
 * 上传图片
 * @returns {Promise} - 返回上传结果
 * @param file
 * @param businessType
 */
export function uploadImage(file, businessType) {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('businessType', businessType)
    return request({
        url: '/extra/image/upload',
        method: 'post',
        // 修复：覆盖公共 application/json 默认值，避免 Axios 将 FormData 转成 JSON。
        // Axios 浏览器适配器会移除此类型头，再由浏览器添加真实 multipart boundary，不手写 boundary。
        // 原配置仅有 url/method/data，完整历史文件见 implementation-history/20261015-multipart。
        headers: {
            'Content-Type': 'multipart/form-data',
            // 文件不能用 JSON.stringify 比较；不同 FormData 都是 {}，会误报连续上传为重复提交。
            repeatSubmit: false
        },
        data: formData
    })
}

/**
 * 下载图片
 * @param {number} imageId - 图片ID
 * @returns {Promise} - 返回下载结果
 */
export function downloadImage(imageId) {
    return request({
        url: `/extra/image/download/${imageId}`,
        method: 'get',
        responseType: 'blob'
    })
}

/**
 * 获取图片预览URL
 * @param {number} imageId - 图片ID
 * @returns {Promise} - 返回预览URL
 */
export function getImagePreviewUrl(imageId) {
    return request({
        url: `/extra/image/preview/${imageId}`,
        method: 'get'
    })
}

/**
 * 删除图片
 * @param {number} imageId - 图片ID
 * @returns {Promise} - 返回删除结果
 */
export function deleteImage(imageId) {
    return request({
        url: `/extra/image/${imageId}`,
        method: 'delete'
    })
}

/**
 * 查询图片存储列表
 * @param {Object} query - 查询参数
 * @returns {Promise} - 返回图片列表
 */
export function listImageStore(query) {
    return request({
        url: '/extra/image/list',
        method: 'get',
        params: query
    })
}

/**
 * 新增图片存储
 * @param {Object} data - 图片存储数据
 * @returns {Promise} - 返回新增结果
 */
export function addImageStore(data) {
    return request({
        url: '/extra/image',
        method: 'post',
        data: data
    })
}

/**
 * 修改图片存储
 * @param {Object} data - 图片存储数据
 * @returns {Promise} - 返回修改结果
 */
export function updateImageStore(data) {
    return request({
        url: '/extra/image',
        method: 'put',
        data: data
    })
}

/**
 * 批量删除图片存储
 * @param {Array<number>} imageIds - 图片ID数组
 * @returns {Promise} - 返回删除结果
 */
export function batchDeleteImageStore(imageIds) {
    return request({
        url: '/extra/image/batch',
        method: 'delete',
        data: imageIds
    })
}

/**
 * 根据图片ID查询图片存储
 * @param {number} imageId - 图片ID
 * @returns {Promise} - 返回图片存储信息
 */
export function getImageStoreById(imageId) {
    return request({
        url: `/extra/image/${imageId}`,
        method: 'get'
    })
}

// export function confirmImage(imageId){
//     return request({
//         url: `/extra/image/confirm/${imageId}`,
//         method: 'put'
//     })
// }
/** 新增：按本页 ID 批量查询授权缩略图，签名仅用于显示。 */
export function getThumbnailUrls(imageIds) {
  return request({ url: '/extra/image/thumbnail-urls', method: 'post', data: imageIds })
}
