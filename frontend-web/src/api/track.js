import request from '@/utils/request'

/**
 * 获取用户某日期的轨迹
 * @param {Object} params - { date } (yyyy-MM-dd格式)
 * @returns {Promise} - List<BizTrack>
 */
export function getTrackByDate(params) {
    return request({
        url: '/tracks/byDate',
        method: 'get',
        params
    })
}

/**
 * 获取用户轨迹列表
 * @param {Object} params - { page, pageSize }
 * @returns {Promise} - IPage<BizTrack>
 */
export function getTrackList(params) {
    return request({
        url: '/tracks/list',
        method: 'get',
        params
    })
}

/**
 * 获取轨迹详情
 * @param {Number} id - 轨迹ID
 * @returns {Promise} - BizTrack
 */
export function getTrackDetail(id) {
    return request({
        url: `/tracks/${id}`,
        method: 'get'
    })
}
