/**
 * 基础配置
 */
const config = {
  // API基础地址 - 后端服务地址
  BASE_URL: 'http://localhost:8080/api',

  // 请求超时时间
  TIMEOUT: 10000,

  // 重试次数
  RETRY_TIMES: 3,

  // 是否开启调试模式
  DEBUG: process.env.NODE_ENV === 'development',

  // 版本号
  VERSION: '1.0.0'
}

export default config
