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
