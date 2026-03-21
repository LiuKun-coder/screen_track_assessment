package org.example.sc_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 重置密码请求DTO
 */
@Data
public class ResetPasswordDTO {
    @NotBlank(message = "手机号不能为空")
    private String phone;

    private String verifyCode;

    private String code;

    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}
