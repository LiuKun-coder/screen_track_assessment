package org.example.sc_backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 轨迹记录表
 */
@Data
@TableName("biz_track")
public class BizTrack {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 日期 */
    private LocalDate date;

    /** 开始时间 */
    private LocalDateTime startTime;

    /** 结束时间 */
    private LocalDateTime endTime;

    /** 里程(km) */
    private BigDecimal distance;

    /** 轨迹点集合 (JSON大字段存储，包含 time, lat, lng, speed) */
    @TableField("track_points")
    private String trackPoints;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
