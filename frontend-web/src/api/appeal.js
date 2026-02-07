import request from '@/utils/request'

/**
 * 提交申诉（用户）
 * @param {Object} data - AppealSubmitDTO
 * @returns {Promise}
 */
export function submitAppeal(data) {
    return request({
        url: '/appeals',
        method: 'post',
        data: {
            ...data,
            platform: 'web' // Web端标识
        }
    })
}

/**
 * 获取我的申诉列表（用户）
 * @param {Object} params - { page, pageSize }
 * @returns {Promise} - IPage<BizAppeal>
 */
export function getMyAppeals(params) {
    return request({
        url: '/appeals/my',
        method: 'get',
        params
    })
}

/**
 * 获取申诉详情
 * @param {Number} id - 申诉ID
 * @returns {Promise} - BizAppeal
 */
export function getAppealDetail(id) {
    return request({
        url: `/appeals/${id}`,
        method: 'get'
    })
}

/**
 * 获取所有申诉列表（管理员）
 * @param {Object} params - { status, page, pageSize }
 * @returns {Promise} - IPage<BizAppeal>
 */
export function getAppealList(params) {
    return request({
        url: '/appeals/list',
        method: 'get',
        params
    })
}

/**
 * 处理申诉（管理员）
 * @param {Number} id - 申诉ID
 * @param {Object} params - { status, replyContent }
 * @returns {Promise}
 */
export function handleAppeal(id, params) {
    return request({
        url: `/appeals/${id}/handle`,
        method: 'put',
        params
    })
}
