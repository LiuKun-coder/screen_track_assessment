/**
 * 常量定义
 */

// 违规类型
export const VIOLATION_TYPES = {
    SPEEDING: { code: 'speeding', label: '超速行驶' },
    ILLEGAL_PARKING: { code: 'illegal_parking', label: '违规停放' },
    RED_LIGHT: { code: 'red_light', label: '闯红灯' },
    WRONG_WAY: { code: 'wrong_way', label: '逆行' }
}

// 违规状态
export const VIOLATION_STATUS = {
    PENDING: { code: 'pending', label: '待处理', color: '#FF9500' },
    PROCESSED: { code: 'processed', label: '已处理', color: '#4CD964' }
}

// 地点选项
export const PLACE_OPTIONS = [
    { code: 'east_gate', label: '东门' },
    { code: 'west_gate', label: '西门' },
    { code: 'south_gate', label: '南门' },
    { code: 'north_gate', label: '北门' },
    { code: 'teaching_building', label: '教学楼' },
    { code: 'dormitory', label: '宿舍区' }
]

// 时间筛选选项
export const TIME_OPTIONS = [
    { code: 'today', label: '今天' },
    { code: 'yesterday', label: '昨天' },
    { code: 'week', label: '本周' },
    { code: 'month', label: '本月' },
    { code: 'custom', label: '自定义' }
]

// 存储键名
export const STORAGE_KEYS = {
    TOKEN: 'token',
    REFRESH_TOKEN: 'refresh_token',
    USER_INFO: 'user_info',
    CACHE_PREFIX: 'cache_'
}

// 页面路径
export const PAGE_PATHS = {
    LOGIN: '/pages/auth/login',
    HOME: '/pages/home/index',
    VIOLATION_REPORT: '/pages/violation/report',
    TRACK_INDEX: '/pages/track/index',
    PROFILE: '/pages/profile/index'
}

export default {
    VIOLATION_TYPES,
    VIOLATION_STATUS,
    PLACE_OPTIONS,
    TIME_OPTIONS,
    STORAGE_KEYS,
    PAGE_PATHS
}
