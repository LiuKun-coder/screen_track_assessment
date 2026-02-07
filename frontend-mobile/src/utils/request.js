/**
 * 网络请求封装
 */
import config from '@/config/index.js'
import { getToken, clearAuth, redirectToLogin } from '@/utils/auth.js'

class Request {
    constructor(options = {}) {
        this.baseURL = options.baseURL || config.BASE_URL
        this.timeout = options.timeout || config.TIMEOUT
        this.requestInterceptors = []
        this.responseInterceptors = []
    }

    /**
     * 添加请求拦截器
     * @param {Function} interceptor 拦截器函数
     */
    addRequestInterceptor(interceptor) {
        this.requestInterceptors.push(interceptor)
    }

    /**
     * 添加响应拦截器
     * @param {Function} interceptor 拦截器函数
     */
    addResponseInterceptor(interceptor) {
        this.responseInterceptors.push(interceptor)
    }

    /**
     * 统一请求方法
     * @param {object} options 请求选项
     * @returns {Promise}
     */
    async request(options) {
        // 应用请求拦截器
        let processedOptions = { ...options }
        for (const interceptor of this.requestInterceptors) {
            processedOptions = await interceptor(processedOptions)
        }

        // 构建完整URL
        const url = processedOptions.url.startsWith('http')
            ? processedOptions.url
            : this.baseURL + processedOptions.url

        return new Promise((resolve, reject) => {
            uni.request({
                url,
                method: processedOptions.method || 'GET',
                data: processedOptions.data,
                header: {
                    'Content-Type': 'application/json',
                    ...processedOptions.header
                },
                timeout: this.timeout,
                success: async (response) => {
                    // 应用响应拦截器
                    let processedResponse = response
                    for (const interceptor of this.responseInterceptors) {
                        try {
                            processedResponse = await interceptor(processedResponse, processedOptions)
                        } catch (error) {
                            reject(error)
                            return
                        }
                    }
                    resolve(processedResponse)
                },
                fail: (error) => {
                    this.handleError(error)
                    reject(error)
                }
            })
        })
    }

    /**
     * 处理错误
     * @param {object} error 错误对象
     */
    handleError(error) {
        console.error('请求错误:', error)

        let message = '网络请求失败'
        if (error.errMsg) {
            if (error.errMsg.includes('timeout')) {
                message = '请求超时，请检查网络'
            } else if (error.errMsg.includes('fail')) {
                message = '网络连接失败'
            }
        }

        uni.showToast({
            title: message,
            icon: 'none',
            duration: 2000
        })
    }

    // 便捷方法
    get(url, data = {}, options = {}) {
        return this.request({ url, method: 'GET', data, ...options })
    }

    post(url, data = {}, options = {}) {
        return this.request({ url, method: 'POST', data, ...options })
    }

    put(url, data = {}, options = {}) {
        return this.request({ url, method: 'PUT', data, ...options })
    }

    delete(url, data = {}, options = {}) {
        return this.request({ url, method: 'DELETE', data, ...options })
    }
}

// 创建请求实例
const request = new Request()

// 添加请求拦截器 - 添加 Token
request.addRequestInterceptor(async (config) => {
    const token = getToken()
    if (token) {
        config.header = {
            ...config.header,
            'Authorization': `Bearer ${token}`
        }
    }
    return config
})

// 添加响应拦截器 - 处理响应
request.addResponseInterceptor(async (response, config) => {
    const { statusCode, data } = response

    // 成功响应
    if (statusCode >= 200 && statusCode < 300) {
        // 如果后端返回统一格式 { code, data, message }
        if (data && typeof data === 'object' && 'code' in data) {
            if (data.code === 0 || data.code === 200) {
                return data
            } else {
                uni.showToast({
                    title: data.message || '请求失败',
                    icon: 'none'
                })
                throw new Error(data.message || '请求失败')
            }
        }
        return data
    }

    // 401 未授权
    if (statusCode === 401) {
        clearAuth()
        redirectToLogin()
        throw new Error('登录已过期，请重新登录')
    }

    // 403 禁止访问
    if (statusCode === 403) {
        uni.showToast({
            title: '权限不足',
            icon: 'none'
        })
        throw new Error('权限不足')
    }

    // 其他错误
    uni.showToast({
        title: data?.message || '请求失败',
        icon: 'none'
    })
    throw new Error(data?.message || `请求失败: ${statusCode}`)
})

export { Request }
export default request
