/**
 * 用户状态管理
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { setToken, setUserInfo, clearAuth, setRefreshToken } from '@/utils/auth.js'
import authApi from '@/api/auth.js'

export const useUserStore = defineStore('user', () => {
    // 状态
    const userInfo = ref(null)
    const token = ref(null)
    const isLoggedIn = ref(false)
    const loading = ref(false)

    // 计算属性
    const displayName = computed(() => {
        return userInfo.value?.nickname || userInfo.value?.username || '未登录'
    })

    const avatar = computed(() => {
        return userInfo.value?.avatar || '/static/images/default-avatar.png'
    })

    // 初始化 - 从本地存储恢复状态
    function init() {
        try {
            const storedToken = uni.getStorageSync('token')
            const storedUserInfo = uni.getStorageSync('user_info')

            if (storedToken) {
                token.value = storedToken
                isLoggedIn.value = true
            }
            if (storedUserInfo) {
                userInfo.value = storedUserInfo
            }
        } catch (error) {
            console.error('初始化用户状态失败:', error)
        }
    }

    // 设置用户信息
    function setUser(info) {
        userInfo.value = info
        isLoggedIn.value = true
        setUserInfo(info)
    }

    // 设置Token
    function updateToken(newToken, refreshToken = null) {
        token.value = newToken
        setToken(newToken)
        if (refreshToken) {
            setRefreshToken(refreshToken)
        }
    }

    // 密码登录
    async function loginByPassword(username, password) {
        loading.value = true
        try {
            const res = await authApi.login({ username, password })

            // 适配后端响应：{ code, message, data: { token, userInfo }, timestamp }
            const { token: accessToken, userInfo: user } = res.data

            updateToken(accessToken)
            setUser(user)

            return { success: true }
        } catch (error) {
            console.error('登录失败:', error)
            return { success: false, error: error.message }
        } finally {
            loading.value = false
        }
    }

    // 微信登录
    async function loginByWechat() {
        loading.value = true
        try {
            // 获取微信登录凭证
            const loginRes = await new Promise((resolve, reject) => {
                uni.login({
                    provider: 'weixin',
                    success: resolve,
                    fail: reject
                })
            })

            // 调用后端微信登录接口
            const res = await authApi.wxLogin({ code: loginRes.code })

            // 适配后端响应
            const { token: accessToken, userInfo: user } = res.data

            updateToken(accessToken)
            setUser(user)

            return { success: true }
        } catch (error) {
            console.error('微信登录失败:', error)
            return { success: false, error: error.message || '微信登录失败' }
        } finally {
            loading.value = false
        }
    }

    // 注册
    async function register(data) {
        loading.value = true
        try {
            const res = await authApi.register(data)
            const message = typeof res?.data === 'string' ? res.data : '注册成功'
            return { success: true, message }
        } catch (error) {
            return { success: false, error: error.message }
        } finally {
            loading.value = false
        }
    }

    // 登出
    async function logout() {
        try {
            await authApi.logout()
        } catch (error) {
            console.error('登出请求失败:', error)
        } finally {
            // 无论是否成功都清除本地状态
            userInfo.value = null
            token.value = null
            isLoggedIn.value = false
            clearAuth()
        }
    }

    // 更新用户信息
    function updateUserInfo(info) {
        userInfo.value = { ...userInfo.value, ...info }
        setUserInfo(userInfo.value)
    }

    return {
        // 状态
        userInfo,
        token,
        isLoggedIn,
        loading,
        // 计算属性
        displayName,
        avatar,
        // 方法
        init,
        setUser,
        updateToken,
        loginByPassword,
        loginByWechat,
        register,
        logout,
        updateUserInfo
    }
})
