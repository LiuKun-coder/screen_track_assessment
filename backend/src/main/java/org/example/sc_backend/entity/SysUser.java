package org.example.sc_backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统用户表 (普通用户/学生)
 */
@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户名/学号 */
    private String username;

    /** 加密后的密码 */
    private String password;

    /** 真实姓名 */
    private String name;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 头像URL */
    private String avatar;

    /** 所属学院/部门 */
    private String department;

    /** 用户类型: student, teacher, staff */
    private String userType;

    /** 状态: active, disabled */
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
