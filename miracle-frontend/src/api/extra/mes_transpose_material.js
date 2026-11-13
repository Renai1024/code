import request from '@/utils/request'
export function list_transpose_material(query) {
    return request({
        url: '/extra/mes/report/mes_transpose_material/list',
        method: 'get',
        params: query
    })
}

export function get_transpose_material(id) {
    return request({
        url: '/extra/mes/report/mes_transpose_material/' + id,
        method: 'get'
    })
}

export function add_transpose_material(data) {
    return request({
        url: '/extra/mes/report/mes_transpose_material/add',
        method: 'post',
        data: data
    })
}

export function addBatch_transpose_material(data) {
    return request({
        url: '/extra/mes/report/mes_transpose_material/addBatch',
        method: 'post',
        data: data
    })
}

export function update_transpose_material(data) {
    return request({
        url: '/extra/mes/report/mes_transpose_material/edit',
        method: 'put',
        data: data
    })
}

export function del_transpose_material(id) {
    return request({
        url: '/extra/mes/report/mes_transpose_material/' + id,
        method: 'delete'
    })
}
