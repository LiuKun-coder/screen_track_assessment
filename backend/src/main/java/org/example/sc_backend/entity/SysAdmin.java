package org.example.sc_backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 系统管理员表
 */
@Data
@TableName("sys_admin")
public class SysAdmin {
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 管理员账号 */
    private String username;

    /** 加密密码 */
    private String password;

    /** 管理员姓名 */
    private String name;

    /** 联系方式 */
    private String phone;

    /** 角色: admin, super_admin */
    private String role;

    /** 所属部门(如保卫处) */
    private String department;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
