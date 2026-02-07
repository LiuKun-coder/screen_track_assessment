import request from '@/utils/request'

/**
 * 获取所有围栏
 * @returns {Promise} - List<BizFence>
 */
export function getFenceList() {
    return request({
        url: '/fences',
        method: 'get'
    })
}

/**
 * 获取围栏详情
 * @param {Number} id - 围栏ID
 * @returns {Promise} - BizFence
 */
export function getFenceDetail(id) {
    return request({
        url: `/fences/${id}`,
        method: 'get'
    })
}

/**
 * 添加围栏（管理员）
 * @param {Object} data - BizFence
 * @returns {Promise}
 */
export function addFence(data) {
    return request({
        url: '/fences',
        method: 'post',
        data
    })
}

/**
 * 更新围栏（管理员）
 * @param {Number} id - 围栏ID
 * @param {Object} data - BizFence
 * @returns {Promise}
 */
export function updateFence(id, data) {
    return request({
        url: `/fences/${id}`,
        method: 'put',
        data
    })
}

/**
 * 删除围栏（管理员）
 * @param {Number} id - 围栏ID
 * @returns {Promise}
 */
export function deleteFence(id) {
    return request({
        url: `/fences/${id}`,
        method: 'delete'
    })
}
