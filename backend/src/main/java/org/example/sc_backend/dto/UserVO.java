package org.example.sc_backend.dto;

import lombok.Data;
import java.util.List;

/**
 * 用户信息VO (脱敏后返回前端)
 */
@Data
public class UserVO {
    private Long id;
    private String username;
    private String name;
    private String phone;
    private String email;
    private String avatar;
    private String department;
    private String userType;
    private String status;
    private String role; // 用于区分admin和user
    private List<String> permissions; // 权限列表
}
