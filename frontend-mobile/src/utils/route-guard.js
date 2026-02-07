/**
 * 路由守卫工具
 * 用于控制页面访问权限和登录拦截
 */

// 无需登录即可访问的页面白名单
const whiteList = [
    '/pages/auth/login',
    '/pages/auth/register',
    '/pages/auth/reset-password'
]

/**
 * 检查页面是否需要登录
 * @param {string} url 页面路径
 * @returns {boolean} true-需要登录, false-无需登录
 */
export function requiresAuth(url) {
    // 如果URL在白名单中,则无需登录
    return !whiteList.some(path => url.includes(path))
}

/**
 * 检查用户是否已登录
 * @returns {boolean} true-已登录, false-未登录
 */
export function isLoggedIn() {
    try {
        const token = uni.getStorageSync('token')
        return !!token
    } catch (error) {
        console.error('检查登录状态失败:', error)
        return false
    }
}

/**
 * 跳转到登录页
 * @param {string} redirect 登录成功后要跳转的页面
 */
export function navigateToLogin(redirect = '') {
    const url = redirect ? `/pages/auth/login?redirect=${encodeURIComponent(redirect)}` : '/pages/auth/login'

    // 使用 reLaunch 清空页面栈,防止用户通过返回键绕过登录
    uni.reLaunch({ url })
}

/**
 * 路由拦截器
 * 在页面跳转前检查是否需要登录
 * @param {string} url 目标页面路径
 * @returns {boolean} true-允许跳转, false-拦截跳转
 */
export function routeGuard(url) {
    // 检查是否需要登录
    if (requiresAuth(url)) {
        // 检查用户是否已登录
        if (!isLoggedIn()) {
            // 未登录,跳转到登录页
            navigateToLogin(url)
            return false
        }
    }
    return true
}
