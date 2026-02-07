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
     */
    uploadTrackPoints(trackPoints) {
        return request.post(API_ENDPOINTS.TRACK.UPLOAD, { trackPoints })
    }
}

export default trackApi
