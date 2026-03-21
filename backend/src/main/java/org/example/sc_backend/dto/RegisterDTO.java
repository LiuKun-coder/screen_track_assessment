package org.example.sc_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 注册请求DTO
 */
@Data
public class RegisterDTO {
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String name;

    private String nickname;

    private String phone;

    private String verifyCode;

    private String code;

    private String email;

    private String department;

    private String userType;
}
