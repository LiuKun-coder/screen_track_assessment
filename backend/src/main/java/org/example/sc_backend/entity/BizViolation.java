package org.example.sc_backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 违规记录表
 */
@Data
@TableName("biz_violation")
public class BizViolation {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联用户ID (若能识别身份) */
    private Long userId;

    /** 冗余用户姓名 */
    private String userName;

    /** 违规时间 */
    private LocalDateTime violationTime;

    /** 违规地点 */
    private String place;

    /** 违规类型: 超速, 违停, 逆行, 闯红灯 */
    private String type;

    /** 详细描述 */
    private String detail;

    /** 处罚结果 */
    private String penalty;

    /** 状态: pending待处理, processed已处理 */
    private String status;

    /** 证据图片URL数组 (JSON格式存储) */
    private String evidenceImages;

    /** 是否可申诉 */
    private Integer canAppeal;

    /** 申诉状态: none, pending, approved, rejected */
    private String appealStatus;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
