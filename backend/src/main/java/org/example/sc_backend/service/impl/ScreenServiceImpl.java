package org.example.sc_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.sc_backend.dto.ScreenStatsVO;
import org.example.sc_backend.entity.BizAppeal;
import org.example.sc_backend.entity.BizViolation;
import org.example.sc_backend.mapper.BizAppealMapper;
import org.example.sc_backend.mapper.BizTrackMapper;
import org.example.sc_backend.mapper.BizViolationMapper;
import org.example.sc_backend.service.ScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Map.entry;

/**
 * 大屏统计服务实现类
 */
@Service
public class ScreenServiceImpl implements ScreenService {

    private static final Map<String, String> TYPE_NAME_MAP = Map.ofEntries(
        entry("speeding", "超速"),
        entry("illegal_parking", "违停"),
        entry("wrong_way", "逆行"),
        entry("red_light", "闯红灯"),
        entry("helmet", "未戴头盔"),
        entry("parking", "违停"),
        entry("Speeding", "超速"),
        entry("Illegal Parking", "违停"),
        entry("Wrong Way", "逆行"),
        entry("Red Light", "闯红灯")
    );

    private static final Map<String, String> STATUS_NAME_MAP = Map.ofEntries(
        entry("pending", "待处理"),
        entry("processing", "处理中"),
        entry("processed", "已处理"),
        entry("approved", "已通过"),
        entry("rejected", "已驳回"),
        entry("none", "无")
    );

    private static final Map<String, double[]> PLACE_COORDINATES = Map.ofEntries(
        entry("东门", new double[]{117.1480, 34.2200}),
        entry("西门", new double[]{117.1320, 34.2145}),
        entry("南门", new double[]{117.1415, 34.2088}),
        entry("北门", new double[]{117.1488, 34.2223}),
        entry("图书馆广场", new double[]{117.1420, 34.2160}),
        entry("教学楼A区", new double[]{117.1380, 34.2180}),
        entry("教学楼", new double[]{117.1392, 34.2171}),
        entry("宿舍区", new double[]{117.1350, 34.2100}),
        entry("体育馆", new double[]{117.1450, 34.2120}),
        entry("食堂", new double[]{117.1360, 34.2120}),
        entry("行政楼", new double[]{117.1402, 34.2140}),
        entry("主干道", new double[]{117.1442, 34.2151}),
        entry("实验楼", new double[]{117.1432, 34.2136}),
        entry("图书馆", new double[]{117.1421, 34.2164}),
        entry("校医院", new double[]{117.1463, 34.2141}),
        entry("学生活动中心", new double[]{117.1373, 34.2132})
    );

    @Autowired
    private BizViolationMapper violationMapper;

    @Autowired
    private BizAppealMapper appealMapper;

    @Autowired
    private BizTrackMapper trackMapper;

    @Override
    public ScreenStatsVO getScreenStats() {
        ScreenStatsVO stats = new ScreenStatsVO();

        // 今日违规数
        stats.setTodayCount(violationMapper.countToday());

        // 本周违规数
        stats.setWeekCount(violationMapper.countThisWeek());

        // 总违规数
        Long total = violationMapper.selectCount(
                new LambdaQueryWrapper<BizViolation>().eq(BizViolation::getDeleted, 0)
        );
        stats.setTotalCount(total.intValue());

        // 待处理数量
        Long pending = violationMapper.selectCount(
                new LambdaQueryWrapper<BizViolation>()
                        .eq(BizViolation::getStatus, "pending")
                        .eq(BizViolation::getDeleted, 0)
        );
        stats.setPendingCount(pending.intValue());

        // 按类型分组统计（补充中文展示字段）
        List<Map<String, Object>> rawTypeStats = violationMapper.groupByType();
        List<Map<String, Object>> typeStats = new ArrayList<>();
        for (Map<String, Object> item : rawTypeStats) {
            String type = toText(item.get("type"));
            int count = toInt(item.get("count"));
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("type", type);
            row.put("name", typeName(type));
            row.put("count", count);
            row.put("value", count);
            typeStats.add(row);
        }
        stats.setTypeStats(typeStats);

        // 每日趋势
        stats.setDailyTrend(violationMapper.dailyTrend());

        // 区域违规排行
        List<Map<String, Object>> areaRanking = violationMapper.areaRanking();
        stats.setAreaRanking(areaRanking);

        // 最近违规列表
        List<Map<String, Object>> recentRows = violationMapper.recentViolations(20);
        List<Map<String, Object>> recentViolations = new ArrayList<>();
        for (Map<String, Object> row : recentRows) {
            String type = toText(row.get("type"));
            String status = toText(row.get("status"));
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", row.get("id"));
            item.put("time", toText(row.get("time")));
            item.put("type", type);
            item.put("typeName", typeName(type));
            item.put("location", toText(row.get("location")));
            item.put("status", status);
            item.put("statusName", statusName(status));
            recentViolations.add(item);
        }
        stats.setRecentViolations(recentViolations);

        // 地图点位（优先使用区域排行；最多展示10个点）
        stats.setViolationPoints(buildViolationPoints(areaRanking, recentRows));

        // 在线设备数（近24小时有轨迹记录的去重用户）
        Integer onlineDevices = trackMapper.countOnlineDevices();
        stats.setOnlineDevices(onlineDevices == null ? 0 : onlineDevices);

        // 申诉处理率
        Long totalAppeals = appealMapper.selectCount(new LambdaQueryWrapper<>());
        Long handledAppeals = appealMapper.selectCount(
                new LambdaQueryWrapper<BizAppeal>().ne(BizAppeal::getStatus, "pending")
        );
        if (totalAppeals > 0) {
            stats.setAppealRate((double) handledAppeals / totalAppeals * 100);
        } else {
            stats.setAppealRate(0.0);
        }

        return stats;
    }

    private List<Map<String, Object>> buildViolationPoints(List<Map<String, Object>> areaRanking,
                                                           List<Map<String, Object>> recentRows) {
        List<Map<String, Object>> points = new ArrayList<>();
        Map<String, Integer> areaCountMap = new LinkedHashMap<>();

        for (Map<String, Object> area : areaRanking) {
            String areaName = toText(area.get("area"));
            if (!areaName.isEmpty()) {
                areaCountMap.put(areaName, toInt(area.get("count")));
            }
        }

        for (Map<String, Object> row : recentRows) {
            String location = toText(row.get("location"));
            if (!location.isEmpty() && !areaCountMap.containsKey(location)) {
                areaCountMap.put(location, 1);
            }
        }

        int id = 1;
        for (Map.Entry<String, Integer> entry : areaCountMap.entrySet()) {
            double[] coordinate = PLACE_COORDINATES.get(entry.getKey());
            if (coordinate == null) {
                continue;
            }
            Map<String, Object> point = new LinkedHashMap<>();
            point.put("id", id++);
            point.put("location", entry.getKey());
            point.put("lng", coordinate[0]);
            point.put("lat", coordinate[1]);
            point.put("count", entry.getValue());
            point.put("type", resolvePointType(entry.getValue()));
            points.add(point);
            if (points.size() >= 10) {
                break;
            }
        }

        return points;
    }

    private String resolvePointType(int count) {
        if (count >= 8) {
            return "danger";
        }
        if (count >= 4) {
            return "warning";
        }
        return "info";
    }

    private String typeName(String type) {
        if (type == null || type.isEmpty()) {
            return "未知类型";
        }
        return TYPE_NAME_MAP.getOrDefault(type, type);
    }

    private String statusName(String status) {
        if (status == null || status.isEmpty()) {
            return "待处理";
        }
        return STATUS_NAME_MAP.getOrDefault(status, status);
    }

    private String toText(Object value) {
        return Objects.toString(value, "").trim();
    }

    private int toInt(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(Objects.toString(value, "0"));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}
