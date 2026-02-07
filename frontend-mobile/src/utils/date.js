/**
 * 日期工具函数
 */

/**
 * 格式化日期
 * @param {Date|string|number} date 日期
 * @param {string} format 格式字符串 YYYY-MM-DD HH:mm:ss
 * @returns {string} 格式化后的日期字符串
 */
export function formatDate(date, format = 'YYYY-MM-DD HH:mm:ss') {
    if (!date) return ''

    const d = new Date(date)
    if (isNaN(d.getTime())) return ''

    const year = d.getFullYear()
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const day = String(d.getDate()).padStart(2, '0')
    const hours = String(d.getHours()).padStart(2, '0')
    const minutes = String(d.getMinutes()).padStart(2, '0')
    const seconds = String(d.getSeconds()).padStart(2, '0')

    return format
        .replace('YYYY', year)
        .replace('MM', month)
        .replace('DD', day)
        .replace('HH', hours)
        .replace('mm', minutes)
        .replace('ss', seconds)
}

/**
 * 获取相对时间描述
 * @param {Date|string|number} date 日期
 * @returns {string} 相对时间描述
 */
export function getRelativeTime(date) {
    if (!date) return ''

    const d = new Date(date)
    if (isNaN(d.getTime())) return ''

    const now = Date.now()
    const diff = now - d.getTime()

    const minute = 60 * 1000
    const hour = 60 * minute
    const day = 24 * hour
    const week = 7 * day
    const month = 30 * day

    if (diff < minute) {
        return '刚刚'
    } else if (diff < hour) {
        return Math.floor(diff / minute) + '分钟前'
    } else if (diff < day) {
        return Math.floor(diff / hour) + '小时前'
    } else if (diff < week) {
        return Math.floor(diff / day) + '天前'
    } else if (diff < month) {
        return Math.floor(diff / week) + '周前'
    } else {
        return formatDate(d, 'YYYY-MM-DD')
    }
}

/**
 * 获取今天的开始时间
 * @returns {Date}
 */
export function getStartOfToday() {
    const d = new Date()
    d.setHours(0, 0, 0, 0)
    return d
}

/**
 * 获取今天的结束时间
 * @returns {Date}
 */
export function getEndOfToday() {
    const d = new Date()
    d.setHours(23, 59, 59, 999)
    return d
}

/**
 * 获取本周的开始日期
 * @returns {Date}
 */
export function getStartOfWeek() {
    const d = new Date()
    const day = d.getDay() || 7 // 周日为0，转为7
    d.setDate(d.getDate() - day + 1)
    d.setHours(0, 0, 0, 0)
    return d
}

/**
 * 获取本月的开始日期
 * @returns {Date}
 */
export function getStartOfMonth() {
    const d = new Date()
    d.setDate(1)
    d.setHours(0, 0, 0, 0)
    return d
}

/**
 * 计算时长（分钟）
 * @param {Date|string} startTime 开始时间
 * @param {Date|string} endTime 结束时间
 * @returns {number} 时长（分钟）
 */
export function calculateDuration(startTime, endTime) {
    const start = new Date(startTime)
    const end = new Date(endTime)

    if (isNaN(start.getTime()) || isNaN(end.getTime())) return 0

    return Math.floor((end - start) / 60000)
}

/**
 * 格式化时长为可读格式
 * @param {number} minutes 分钟数
 * @returns {string} 格式化后的时长字符串
 */
export function formatDuration(minutes) {
    if (!minutes) return '0分钟'

    const hours = Math.floor(minutes / 60)
    const mins = minutes % 60

    if (hours > 0) {
        return `${hours}小时${mins}分钟`
    }
    return `${mins}分钟`
}

export default {
    formatDate,
    getRelativeTime,
    getStartOfToday,
    getEndOfToday,
    getStartOfWeek,
    getStartOfMonth,
    calculateDuration,
    formatDuration
}
