/**
 * 认证工具
 */
import { STORAGE_KEYS, PAGE_PATHS } from '@/config/constant.js'

/**
 * 获取 Token
 * @returns {string|null}
 */
export function getToken() {
    try {
        return uni.getStorageSync(STORAGE_KEYS.TOKEN) || null
    } catch (error) {
        console.error('获取Token失败:', error)
        return null
    }
}

/**
 * 设置 Token
 * @param {string} token
 */
export function setToken(token) {
    try {
        uni.setStorageSync(STORAGE_KEYS.TOKEN, token)
    } catch (error) {
        console.error('设置Token失败:', error)
    }
}

/**
 * 获取 Refresh Token
 * @returns {string|null}
 */
export function getRefreshToken() {
    try {
        return uni.getStorageSync(STORAGE_KEYS.REFRESH_TOKEN) || null
    } catch (error) {
        console.error('获取RefreshToken失败:', error)
        return null
    }
}

/**
 * 设置 Refresh Token
 * @param {string} refreshToken
 */
export function setRefreshToken(refreshToken) {
    try {
        uni.setStorageSync(STORAGE_KEYS.REFRESH_TOKEN, refreshToken)
    } catch (error) {
        console.error('设置RefreshToken失败:', error)
    }
}

/**
 * 获取用户信息
 * @returns {object|null}
 */
export function getUserInfo() {
    try {
        const userInfo = uni.getStorageSync(STORAGE_KEYS.USER_INFO)
        return userInfo || null
    } catch (error) {
        console.error('获取用户信息失败:', error)
        return null
    }
}

/**
 * 设置用户信息
 * @param {object} userInfo
 */
export function setUserInfo(userInfo) {
    try {
        uni.setStorageSync(STORAGE_KEYS.USER_INFO, userInfo)
    } catch (error) {
        console.error('设置用户信息失败:', error)
    }
}

/**
 * 检查是否已登录
 * @returns {boolean}
 */
export function isLoggedIn() {
    return !!getToken()
}

/**
 * 清除认证信息
 */
export function clearAuth() {
    try {
        uni.removeStorageSync(STORAGE_KEYS.TOKEN)
        uni.removeStorageSync(STORAGE_KEYS.REFRESH_TOKEN)
        uni.removeStorageSync(STORAGE_KEYS.USER_INFO)
    } catch (error) {
        console.error('清除认证信息失败:', error)
    }
}

/**
 * 跳转到登录页
 */
export function redirectToLogin() {
    // 获取当前页面路径
    const pages = getCurrentPages()
    const currentPage = pages[pages.length - 1]
    const currentPath = currentPage ? '/' + currentPage.route : ''

    // 不重复跳转登录页
    if (currentPath === PAGE_PATHS.LOGIN) {
        return
    }

    uni.reLaunch({
        url: PAGE_PATHS.LOGIN
    })
}

/**
 * 登录成功后跳转
 * @param {string} redirectUrl 重定向地址
 */
export function redirectAfterLogin(redirectUrl) {
    const url = redirectUrl || PAGE_PATHS.HOME
    uni.switchTab({
        url,
        fail: () => {
            // 如果不是 tabBar 页面，使用 reLaunch
            uni.reLaunch({ url })
        }
    })
}

/**
 * 检查页面是否需要登录
 * @param {string} path 页面路径
 * @returns {boolean}
 */
export function requiresAuth(path) {
    // 不需要登录的页面白名单
    const whiteList = [
        '/pages/auth/login',
        '/pages/auth/register',
        '/pages/auth/reset-password'
    ]

    return !whiteList.some(item => path.includes(item))
}

export default {
    getToken,
    setToken,
    getRefreshToken,
    setRefreshToken,
    getUserInfo,
    setUserInfo,
    isLoggedIn,
    clearAuth,
    redirectToLogin,
    redirectAfterLogin,
    requiresAuth
}
