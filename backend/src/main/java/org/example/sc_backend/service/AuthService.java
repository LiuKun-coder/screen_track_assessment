package org.example.sc_backend.service;

import org.example.sc_backend.dto.LoginDTO;
import org.example.sc_backend.dto.LoginVO;
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
}
