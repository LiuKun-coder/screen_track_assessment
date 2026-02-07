import request from '@/utils/request'

/**
 * 获取大屏统计数据（管理员）
 * @returns {Promise} - ScreenStatsVO
 */
export function getScreenStats() {
    return request({
        url: '/screen/stats',
        method: 'get'
    })
}
