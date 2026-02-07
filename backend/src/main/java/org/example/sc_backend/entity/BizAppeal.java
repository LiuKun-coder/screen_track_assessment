package org.example.sc_backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 申诉记录表
 */
@Data
@TableName("biz_appeal")
public class BizAppeal {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 关联违规ID */
    private Long violationId;

    /** 提交人ID */
    private Long userId;

    /** 申诉编号 AP2024... */
    private String appealNumber;

    /** 来源: web, miniapp */
    private String platform;

    /** 申诉原因类型 */
    private String reason;

    /** 申诉描述 */
    private String description;

    /** 联系电话 */
    private String contactPhone;

    /** 证据文件 (视频/图片) JSON */
    private String evidenceFiles;

    /** 状态: pending, approved, rejected */
    private String status;

    /** 管理员处理回复 */
    private String replyContent;

    /** 处理管理员ID */
    private Long handlerId;

    /** 处理时间 */
    private LocalDateTime handleTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
