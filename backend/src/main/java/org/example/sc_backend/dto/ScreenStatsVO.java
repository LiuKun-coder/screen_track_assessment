package org.example.sc_backend.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 大屏统计VO
 */
@Data
public class ScreenStatsVO {
    /** 今日违规总数 */
    private Integer todayCount;

    /** 本周违规总数 */
    private Integer weekCount;

    /** 总违规数 */
    private Integer totalCount;

    /** 待处理数量 */
    private Integer pendingCount;

    /** 按违规类型分组统计 [{type: "超速", count: 10}, ...] */
    private List<Map<String, Object>> typeStats;

    /** 最近7天每日趋势 [{date: "2024-01-01", count: 5}, ...] */
    private List<Map<String, Object>> dailyTrend;

    /** 申诉处理率 */
    private Double appealRate;

    /** 在线设备数（基于近24小时轨迹活跃用户） */
    private Integer onlineDevices;

    /** 区域违规排行 [{area: "教学楼", count: 23}, ...] */
    private List<Map<String, Object>> areaRanking;

    /** 地图违规点位 [{location: "北门", lng: 117.1, lat: 34.2, type: "warning"}, ...] */
    private List<Map<String, Object>> violationPoints;

    /** 实时违规列表 [{time: "2026-04-06 10:22:00", typeName: "超速", ...}, ...] */
    private List<Map<String, Object>> recentViolations;
}
