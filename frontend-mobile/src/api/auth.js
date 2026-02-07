/**
 * 认证相关 API
 */
import request from '@/utils/request.js'
import { API_ENDPOINTS } from '@/config/api.js'

const authApi = {
    /**
     * 密码登录
     * @param {object} data { username, password }
     */
    login(data) {
        return request.post(API_ENDPOINTS.AUTH.LOGIN, data)
    },

    /**
     * 微信登录
     * @param {object} data { code, nickName, avatarUrl }
     */
    wxLogin(data) {
        return request.post(API_ENDPOINTS.AUTH.WECHAT_LOGIN, data)
    },

    /**
     * 获取当前用户信息
     */
    getUserInfo() {
        return request.get(API_ENDPOINTS.AUTH.USER_INFO)
    },

    /**
     * 注册
     * @param {object} data { phone, password, code, nickname }
     */
    register(data) {
        return request.post(API_ENDPOINTS.AUTH.REGISTER, data)
    },

    /**
     * 登出
     */
    logout() {
        return request.post(API_ENDPOINTS.AUTH.LOGOUT)
    },

    /**
     * 刷新 Token
     * @param {string} refreshToken
     */
    refreshToken(refreshToken) {
        return request.post(API_ENDPOINTS.AUTH.REFRESH_TOKEN, { refreshToken })
    },

    /**
     * 发送验证码
     * @param {object} data { phone, type: 'register' | 'reset' | 'login' }
     */
    sendCode(data) {
        return request.post(API_ENDPOINTS.AUTH.SEND_CODE, data)
    },

    /**
     * 重置密码
     * @param {object} data { phone, code, newPassword }
     */
    resetPassword(data) {
        return request.post(API_ENDPOINTS.AUTH.RESET_PASSWORD, data)
    }
}

export default authApi
