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
}
