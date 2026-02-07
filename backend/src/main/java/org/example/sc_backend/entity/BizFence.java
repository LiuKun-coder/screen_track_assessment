package org.example.sc_backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 电子围栏配置表
 */
@Data
@TableName("biz_fence")
public class BizFence {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 围栏名称 */
    private String name;

    /** 范围文字描述 */
    private String rangeDesc;

    /** 围栏坐标点集合 (JSON: [{lat,lng},...]) */
    private String coordinates;

    /** 状态 */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
