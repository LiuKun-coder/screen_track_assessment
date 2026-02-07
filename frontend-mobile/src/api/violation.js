/**
 * 违规相关 API
 */
import request from '@/utils/request.js'
import { API_ENDPOINTS } from '@/config/api.js'

const violationApi = {
    /**
     * 获取我的违规记录
     * @param {object} params { page, pageSize, type, status, startTime, endTime }
     */
    getMyViolations(params) {
        return request.get(API_ENDPOINTS.VIOLATION.MY, params)
    },

    /**
     * 获取违规详情
     * @param {string|number} id 违规ID
     */
    getViolationDetail(id) {
        const url = API_ENDPOINTS.VIOLATION.DETAIL.replace('{id}', id)
        return request.get(url)
    }
}

export default violationApi
