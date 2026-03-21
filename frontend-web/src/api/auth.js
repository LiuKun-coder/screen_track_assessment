import request from '@/utils/request'

/**
 * 用户登录
 * @param {Object} data - { username, password }
 * @returns {Promise} - { token, userInfo }
 */
export function login(data) {
    return request({
        url: '/auth/login',
        method: 'post',
        data
    })
}

/**
 * 获取当前用户信息
 * @returns {Promise} - userInfo
 */
export function getUserInfo() {
    return request({
        url: '/auth/userInfo',
        method: 'get'
    })
}

/**
 * 用户注册
 * @param {Object} data - { username, password, name, phone, email, department, userType }
 * @returns {Promise}
 */
export function register(data) {
    return request({
        url: '/auth/register',
        method: 'post',
        data
    })
}

/**
 * 重置密码
 * @param {Object} data - { phone, verifyCode, newPassword }
 * @returns {Promise}
 */
export function resetPassword(data) {
    return request({
        url: '/auth/reset-password',
        method: 'post',
        data
    })
}

/**
 * 发送短信验证码
 * @param {Object} data - { phone, type: 'register' | 'reset' | 'login' }
 * @returns {Promise}
 */
export function sendCode(data) {
    return request({
        url: '/auth/sendCode',
        method: 'post',
        data
    })
}

/**
 * 更新个人资料
 * @param {Object} data
 * @returns {Promise}
 */
export function updateProfile(data) {
    return request({
        url: '/users/profile',
        method: 'put',
        data
    })
}
