import request from '@/utils/request'

function normalizeViolationQuery(params = {}) {
    const normalized = { ...params }
    if (normalized.startTime && !normalized.startDate) {
        normalized.startDate = normalized.startTime
    }
    if (normalized.endTime && !normalized.endDate) {
        normalized.endDate = normalized.endTime
    }
    return normalized
}

/**
 * 获取违规列表（大厅/管理员）
 * @param {Object} params - { page, pageSize, type, status, startTime, endTime }
 * @returns {Promise} - IPage<ViolationVO>
 */
export function getViolationList(params) {
    return request({
        url: '/violations/reports',
        method: 'get',
        params: normalizeViolationQuery(params)
    })
}

/**
 * 获取我的违规记录（用户）
 * @param {Object} params - { page, pageSize, type, status, startTime, endTime }
 * @returns {Promise} - IPage<ViolationVO>
 */
export function getMyViolations(params) {
    return request({
        url: '/violations/my',
        method: 'get',
        params
    })
}

/**
 * 获取违规详情
 * @param {Number} id - 违规记录ID
 * @returns {Promise} - ViolationVO
 */
export function getViolationDetail(id) {
    return request({
        url: `/violations/${id}`,
        method: 'get'
    })
}

/**
 * 添加违规记录（管理员）
 * @param {Object} data - BizViolation
 * @returns {Promise}
 */
export function addViolation(data) {
    return request({
        url: '/violations',
        method: 'post',
        data
    })
}

/**
 * 更新违规记录（管理员）
 * @param {Number} id - 违规记录ID
 * @param {Object} data - BizViolation
 * @returns {Promise}
 */
export function updateViolation(id, data) {
    return request({
        url: `/violations/${id}`,
        method: 'put',
        data
    })
}

/**
 * 获取违规统计
 * @returns {Promise}
 */
export function getStatistics() {
    return request({
        url: '/violations/statistics',
        method: 'get'
    })
}
