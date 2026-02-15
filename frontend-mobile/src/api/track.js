/**
 * 轨迹相关 API
 */
import request from '@/utils/request.js'
import { API_ENDPOINTS } from '@/config/api.js'

const trackApi = {
    /**
     * 按日期获取轨迹
     * @param {string} date 日期 yyyy-MM-dd
     */
    getTrackByDate(date) {
        return request.get(API_ENDPOINTS.TRACK.BY_DATE, { date })
    },

    /**
     * 获取轨迹历史（用于轨迹回放）
     * @param {object} params { date | startTime | endTime | userId | vehicleNo }
     */
    getHistory(params) {
        const query = { ...params }
        if (!query.date) {
            const source = query.startTime || query.endTime
            if (source && typeof source === 'string') {
                query.date = source.slice(0, 10)
            }
        }
        if (!query.date) {
            query.date = new Date().toISOString().slice(0, 10)
        }
        return request.get(API_ENDPOINTS.TRACK.BY_DATE, { date: query.date })
    },

    /**
     * 获取轨迹列表
     * @param {object} params { page, pageSize }
     */
    getTrackList(params) {
        return request.get(API_ENDPOINTS.TRACK.LIST, params)
    },

    /**
     * 获取轨迹详情
     * @param {string|number} id 轨迹ID
     */
    getTrackDetail(id) {
        const url = API_ENDPOINTS.TRACK.DETAIL.replace('{id}', id)
        return request.get(url)
    },

    /**
     * 上传轨迹点（批量）
     * @param {array} trackPoints 轨迹点数组
     * @param {object} options { date, userId }
     */
    uploadTrackPoints(trackPoints, options = {}) {
        return request.post(API_ENDPOINTS.TRACK.UPLOAD, {
            userId: options.userId,
            date: options.date,
            trackPoints
        })
    }
}

export default trackApi
