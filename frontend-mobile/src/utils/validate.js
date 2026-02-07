/**
 * 表单验证工具
 */

/**
 * 验证手机号
 * @param {string} phone 手机号
 * @returns {boolean}
 */
export function isValidPhone(phone) {
    const phoneReg = /^1[3-9]\d{9}$/
    return phoneReg.test(phone)
}

/**
 * 验证邮箱
 * @param {string} email 邮箱
 * @returns {boolean}
 */
export function isValidEmail(email) {
    const emailReg = /^[\w-]+(\.[\w-]+)*@[\w-]+(\.[\w-]+)+$/
    return emailReg.test(email)
}

/**
 * 验证密码强度
 * @param {string} password 密码
 * @returns {object} { valid: boolean, message: string }
 */
export function validatePassword(password) {
    if (!password) {
        return { valid: false, message: '请输入密码' }
    }
    if (password.length < 6) {
        return { valid: false, message: '密码长度不能少于6位' }
    }
    if (password.length > 20) {
        return { valid: false, message: '密码长度不能超过20位' }
    }
    return { valid: true, message: '' }
}

/**
 * 验证用户名
 * @param {string} username 用户名
 * @returns {object} { valid: boolean, message: string }
 */
export function validateUsername(username) {
    if (!username) {
        return { valid: false, message: '请输入用户名' }
    }
    if (username.length < 3) {
        return { valid: false, message: '用户名长度不能少于3位' }
    }
    if (username.length > 20) {
        return { valid: false, message: '用户名长度不能超过20位' }
    }
    const usernameReg = /^[a-zA-Z0-9_]+$/
    if (!usernameReg.test(username)) {
        return { valid: false, message: '用户名只能包含字母、数字和下划线' }
    }
    return { valid: true, message: '' }
}

/**
 * 验证验证码
 * @param {string} code 验证码
 * @returns {boolean}
 */
export function isValidCode(code) {
    const codeReg = /^\d{4,6}$/
    return codeReg.test(code)
}

/**
 * 验证身份证号
 * @param {string} idCard 身份证号
 * @returns {boolean}
 */
export function isValidIdCard(idCard) {
    const idCardReg = /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/
    return idCardReg.test(idCard)
}

/**
 * 验证车牌号
 * @param {string} plateNumber 车牌号
 * @returns {boolean}
 */
export function isValidPlateNumber(plateNumber) {
    const plateReg = /^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{4,5}[A-HJ-NP-Z0-9挂学警港澳]$/
    return plateReg.test(plateNumber)
}

/**
 * 通用非空验证
 * @param {any} value 值
 * @returns {boolean}
 */
export function isEmpty(value) {
    if (value === null || value === undefined) return true
    if (typeof value === 'string') return value.trim() === ''
    if (Array.isArray(value)) return value.length === 0
    if (typeof value === 'object') return Object.keys(value).length === 0
    return false
}

export default {
    isValidPhone,
    isValidEmail,
    validatePassword,
    validateUsername,
    isValidCode,
    isValidIdCard,
    isValidPlateNumber,
    isEmpty
}
