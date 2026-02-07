import axios from 'axios'
import { ElMessage } from 'element-plus'

// API基础URL
const BASE_URL = 'http://localhost:8080/api'

// 创建axios实例
const request = axios.create({
  baseURL: BASE_URL,
  timeout: 10000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
request.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code !== 200) {
      // 错误处理
      if (res.code === 401) {
        // Token过期，跳转登录
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        localStorage.removeItem('isLoggedIn')
        window.location.href = '/'
        ElMessage.error('登录已过期，请重新登录')
      } else {
        ElMessage.error(res.message || '请求失败')
      }
      return Promise.reject(new Error(res.message || 'Error'))
    }
    return res.data
  },
  error => {
    if (error.response && error.response.status === 401) {
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      localStorage.removeItem('isLoggedIn')
      window.location.href = '/'
      ElMessage.error('登录已过期，请重新登录')
    } else {
      ElMessage.error(error.message || '网络错误')
    }
    return Promise.reject(error)
  }
)

export default request
