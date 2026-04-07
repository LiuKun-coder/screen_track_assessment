import request from '@/utils/request'

const TYPE_NAME_MAP = {
    speeding: '超速',
    illegal_parking: '违停',
    wrong_way: '逆行',
    red_light: '闯红灯',
    helmet: '未戴头盔',
    parking: '违停',
    Speeding: '超速',
    'Illegal Parking': '违停',
    'Wrong Way': '逆行',
    'Red Light': '闯红灯'
}

const STATUS_NAME_MAP = {
    pending: '待处理',
    processing: '处理中',
    processed: '已处理',
    approved: '已通过',
    rejected: '已驳回',
    none: '无'
}

function toNumber(value, fallback = 0) {
    if (typeof value === 'number' && Number.isFinite(value)) {
        return value
    }
    const parsed = Number(value)
    return Number.isFinite(parsed) ? parsed : fallback
}

function mapTypeName(type) {
    if (!type) return '未知类型'
    return TYPE_NAME_MAP[type] || type
}

function mapStatusName(status) {
    if (!status) return '待处理'
    return STATUS_NAME_MAP[status] || status
}

function normalizeTypeDistribution(typeDistribution) {
    if (!Array.isArray(typeDistribution)) return []
    return typeDistribution.map((item, index) => {
        const type = item.type || item.code || ''
        const value = toNumber(item.value ?? item.count, 0)
        return {
            ...item,
            type,
            name: item.name || mapTypeName(type) || `类型${index + 1}`,
            value,
            count: value
        }
    })
}

function normalizeRecentViolations(list) {
    if (!Array.isArray(list)) return []
    return list.map((item, index) => {
        const type = item.type || item.code || ''
        const status = item.status || 'pending'
        return {
            id: item.id ?? index + 1,
            time: item.time || item.violationTime || '',
            type,
            typeName: item.typeName || mapTypeName(type),
            location: item.location || item.place || '未知地点',
            status,
            statusName: item.statusName || mapStatusName(status)
        }
    })
}

function normalizePoints(list) {
    if (!Array.isArray(list)) return []
    return list
        .map((item, index) => ({
            id: item.id ?? index + 1,
            location: item.location || item.name || item.place || '未知点位',
            lng: toNumber(item.lng ?? item.longitude, NaN),
            lat: toNumber(item.lat ?? item.latitude, NaN),
            type: item.type || 'info',
            count: toNumber(item.count, 0)
        }))
        .filter(item => Number.isFinite(item.lng) && Number.isFinite(item.lat))
}

function normalizeScreenStats(payload = {}) {
    const typeStats = Array.isArray(payload.typeStats) ? payload.typeStats : []
    const dailyTrend = Array.isArray(payload.dailyTrend) ? payload.dailyTrend : []
    const typeDistribution = normalizeTypeDistribution(payload.typeDistribution ?? typeStats)
    const recentViolations = normalizeRecentViolations(payload.recentViolations)
    const violationPoints = normalizePoints(payload.violationPoints)
    const areaRanking = Array.isArray(payload.areaRanking) ? payload.areaRanking : []

    return {
        ...payload,
        todayViolations: toNumber(payload.todayViolations ?? payload.todayCount, 0),
        totalViolations: toNumber(payload.totalViolations ?? payload.totalCount, 0),
        processingRate: Number(toNumber(payload.processingRate ?? payload.appealRate, 0).toFixed(1)),
        onlineDevices: toNumber(payload.onlineDevices, 0),
        typeDistribution,
        dailyTrend,
        areaRanking,
        violationPoints,
        recentViolations
    }
}

/**
 * 获取大屏统计数据（管理员）
 * @returns {Promise} - ScreenStatsVO
 */
export function getScreenStats() {
    return request({
        url: '/screen/stats',
        method: 'get'
    }).then(normalizeScreenStats)
}
