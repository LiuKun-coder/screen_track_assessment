package org.example.sc_backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.sc_backend.entity.BizViolation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface BizViolationMapper extends BaseMapper<BizViolation> {

    /**
     * 统计今日违规数量
     */
    @Select("SELECT COUNT(*) FROM biz_violation WHERE DATE(create_time) = CURDATE() AND deleted = 0")
    Integer countToday();

    /**
     * 统计本周违规数量
     */
    @Select("SELECT COUNT(*) FROM biz_violation WHERE YEARWEEK(create_time, 1) = YEARWEEK(CURDATE(), 1) AND deleted = 0")
    Integer countThisWeek();

    /**
     * 按违规类型分组统计
     */
    @Select("SELECT type, COUNT(*) as count FROM biz_violation WHERE deleted = 0 GROUP BY type")
    List<Map<String, Object>> groupByType();

    /**
     * 最近7天每日违规趋势
     */
        @Select("SELECT DATE(create_time) as date, COUNT(*) as count, " +
            "SUM(CASE WHEN status = 'processed' THEN 1 ELSE 0 END) as processed " +
            "FROM biz_violation " +
            "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND deleted = 0 " +
            "GROUP BY DATE(create_time) ORDER BY date")
    List<Map<String, Object>> dailyTrend();

        /**
         * 区域违规排行
         */
        @Select("SELECT place as area, COUNT(*) as count FROM biz_violation " +
            "WHERE deleted = 0 AND place IS NOT NULL AND place <> '' " +
            "GROUP BY place ORDER BY count DESC LIMIT 8")
        List<Map<String, Object>> areaRanking();

        /**
         * 最近违规列表
         */
        @Select("SELECT id, DATE_FORMAT(violation_time, '%Y-%m-%d %H:%i:%s') as time, type, place as location, status " +
            "FROM biz_violation WHERE deleted = 0 ORDER BY violation_time DESC LIMIT #{limit}")
        List<Map<String, Object>> recentViolations(@Param("limit") Integer limit);
}
