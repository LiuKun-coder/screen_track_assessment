package org.example.sc_backend.service;

import org.example.sc_backend.dto.LoginDTO;
import org.example.sc_backend.dto.LoginVO;
import org.example.sc_backend.dto.RegisterDTO;
import org.example.sc_backend.dto.ResetPasswordDTO;
import org.example.sc_backend.dto.UserVO;

/**
 * 认证服务接口
 */
public interface AuthService {
    /**
     * 用户登录
     */
    LoginVO login(LoginDTO loginDTO);

    /**
     * 获取当前用户信息
     */
    UserVO getUserInfo(Long userId, String role);

    /**
     * 用户注册
     */
    String register(RegisterDTO registerDTO);

    /**
     * 重置密码
     */
    String resetPassword(ResetPasswordDTO resetPasswordDTO);

    /**
     * 发送短信验证码
     */
    void sendCode(String phone, String type);
}
