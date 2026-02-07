/**
 * 电子围栏相关 API
 */
import request from '@/utils/request.js'
import { API_ENDPOINTS } from '@/config/api.js'

const fenceApi = {
    /**
     * 获取所有围栏
     */
    getFenceList() {
        return request.get(API_ENDPOINTS.FENCE.LIST)
    },

    /**
     * 获取围栏详情
     * @param {string|number} id 围栏ID
     */
    getFenceDetail(id) {
        const url = API_ENDPOINTS.FENCE.DETAIL.replace('{id}', id)
        return request.get(url)
    }
}

export default fenceApi
