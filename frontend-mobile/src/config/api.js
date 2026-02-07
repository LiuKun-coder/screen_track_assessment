/**
 * API端点配置
 */
export const API_ENDPOINTS = {
    // 认证相关
    AUTH: {
        LOGIN: '/auth/login',                    // 用户名密码登录
        WECHAT_LOGIN: '/auth/wechat/login',     // 微信授权登录（需后端开发）
        USER_INFO: '/auth/userInfo',            // 获取用户信息
        SEND_CODE: '/auth/sendCode',            // 发送验证码
        RESET_PASSWORD: '/auth/reset-password'  // 重置密码
    },

    // 违规相关（仅用户功能）
    VIOLATION: {
        MY: '/violations/my',                    // 我的违规记录
        DETAIL: '/violations/{id}'               // 违规详情
    },

    // 申诉相关
    APPEAL: {
        SUBMIT: '/appeals',                      // 提交申诉
        MY: '/appeals/my',                       // 我的申诉列表
        DETAIL: '/appeals/{id}'                  // 申诉详情
    },

    // 轨迹相关
    TRACK: {
        BY_DATE: '/tracks/byDate',               // 按日期查询轨迹
        LIST: '/tracks/list',                    // 轨迹列表
        DETAIL: '/tracks/{id}',                  // 轨迹详情
        UPLOAD: '/tracks/upload'                 // 上传轨迹点（需后端开发）
    },

    // 电子围栏相关
    FENCE: {
        LIST: '/fences',                         // 所有围栏
        DETAIL: '/fences/{id}'                   // 围栏详情
    },

    // 消息通知相关（需后端开发）
    NOTIFICATION: {
        SUBSCRIBE: '/notification/subscribe',    // 订阅推送
        LIST: '/notifications',                  // 获取消息列表
        READ: '/notifications/{id}/read'         // 标记消息已读
    },

    // 文件上传
    UPLOAD: '/upload'                            // 文件上传
}

export default API_ENDPOINTS
