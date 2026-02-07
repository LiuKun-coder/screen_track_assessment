/**
 * 申诉相关 API
 */
import request from '@/utils/request.js'
import { API_ENDPOINTS } from '@/config/api.js'

const appealApi = {
    /**
     * 提交申诉
     * @param {object} data { violationId, reason, description, contactPhone, evidenceFiles }
     */
    submitAppeal(data) {
        return request.post(API_ENDPOINTS.APPEAL.SUBMIT, {
            ...data,
            platform: 'miniapp'  // 固定标记为小程序端
        })
    },

    /**
     * 获取我的申诉列表
     * @param {object} params { page, pageSize, status }
     */
    getMyAppeals(params) {
        return request.get(API_ENDPOINTS.APPEAL.MY, params)
    },

    /**
     * 获取申诉详情
     * @param {string|number} id 申诉ID
     */
    getAppealDetail(id) {
        const url = API_ENDPOINTS.APPEAL.DETAIL.replace('{id}', id)
        return request.get(url)
    }
}

export default appealApi
