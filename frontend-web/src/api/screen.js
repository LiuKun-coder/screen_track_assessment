import request from '@/utils/request'

function normalizeScreenStats(payload = {}) {
    const typeStats = Array.isArray(payload.typeStats) ? payload.typeStats : []
    const dailyTrend = Array.isArray(payload.dailyTrend) ? payload.dailyTrend : []

    return {
        ...payload,
        todayViolations: payload.todayViolations ?? payload.todayCount ?? 0,
        totalViolations: payload.totalViolations ?? payload.totalCount ?? 0,
        processingRate: payload.processingRate ?? payload.appealRate ?? 0,
        onlineDevices: payload.onlineDevices ?? 0,
        typeDistribution: payload.typeDistribution ?? typeStats,
        dailyTrend,
        areaRanking: payload.areaRanking ?? [],
        violationPoints: payload.violationPoints ?? [],
        recentViolations: payload.recentViolations ?? []
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
