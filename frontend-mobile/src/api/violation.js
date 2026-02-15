/**
 * 违规相关 API
 */
import request from '@/utils/request.js'
import { API_ENDPOINTS } from '@/config/api.js'

function normalizePageResult(result) {
    const payload = result?.data || {}
    if (Array.isArray(payload.records) && !Array.isArray(payload.list)) {
        return {
            ...result,
            data: {
                ...payload,
                list: payload.records
            }
        }
    }
    return result
}

function normalizeStatisticsResult(result) {
    const payload = result?.data || {}
    return {
        ...result,
        data: {
            ...payload,
            totalViolations: payload.totalViolations ?? payload.totalCount ?? 0,
            myViolations: payload.myViolations ?? payload.myCount ?? 0,
            pending: payload.pending ?? payload.pendingCount ?? 0
        }
    }
}

const violationApi = {
    /**
     * 获取我的违规记录
     * @param {object} params { page, pageSize, type, status, startTime, endTime }
     */
    getMyViolations(params) {
        return request.get(API_ENDPOINTS.VIOLATION.MY, params).then(normalizePageResult)
    },

    /**
     * 获取违规通报分页
     * @param {object} params { page, pageSize, type, status, startDate, endDate }
     */
    getViolations(params) {
        return request.get(API_ENDPOINTS.VIOLATION.REPORTS, params).then(normalizePageResult)
    },

    /**
     * 获取违规统计
     */
    getStatistics() {
        return request.get(API_ENDPOINTS.VIOLATION.STATISTICS).then(normalizeStatisticsResult)
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
