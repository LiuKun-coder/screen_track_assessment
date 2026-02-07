/**
 * JSON 数据解析工具
 * 用于解析后端返回的 JSON 字符串字段
 */

/**
 * 安全解析 JSON 字符串
 * @param {string} jsonStr JSON 字符串
 * @param {any} defaultValue 默认值
 * @returns {any} 解析后的数据或默认值
 */
function safeJSONParse(jsonStr, defaultValue = null) {
    if (!jsonStr) return defaultValue

    try {
        return JSON.parse(jsonStr)
    } catch (error) {
        console.error('JSON 解析失败:', error)
        return defaultValue
    }
}

/**
 * 解析证据图片数组
 * @param {string} evidenceImages JSON 字符串
 * @returns {array} 图片 URL 数组
 */
export function parseEvidenceImages(evidenceImages) {
    return safeJSONParse(evidenceImages, [])
}

/**
 * 解析轨迹点数组
 * @param {string} trackPoints JSON 字符串
 * @returns {array} 轨迹点数组 [{time, lat, lng, speed}, ...]
 */
export function parseTrackPoints(trackPoints) {
    return safeJSONParse(trackPoints, [])
}

/**
 * 解析围栏坐标数组
 * @param {string} coordinates JSON 字符串
 * @returns {array} 坐标数组 [{lat, lng}, ...]
 */
export function parseCoordinates(coordinates) {
    return safeJSONParse(coordinates, [])
}

/**
 * 解析申诉证据文件数组
 * @param {string} evidenceFiles JSON 字符串
 * @returns {array} 文件 URL 数组
 */
export function parseEvidenceFiles(evidenceFiles) {
    return safeJSONParse(evidenceFiles, [])
}

export default {
    parseEvidenceImages,
    parseTrackPoints,
    parseCoordinates,
    parseEvidenceFiles
}
