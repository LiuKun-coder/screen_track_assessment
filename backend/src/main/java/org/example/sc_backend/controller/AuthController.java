package org.example.sc_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.sc_backend.common.exception.BusinessException;
import org.example.sc_backend.common.utils.JwtUtils;
import org.example.sc_backend.common.utils.Result;
import org.example.sc_backend.dto.LoginDTO;
import org.example.sc_backend.dto.LoginVO;
import org.example.sc_backend.dto.RegisterDTO;
import org.example.sc_backend.dto.ResetPasswordDTO;
import org.example.sc_backend.dto.UserVO;
import org.example.sc_backend.service.AuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtils jwtUtils;

    @Value("${jwt.token-header}")
    private String tokenHeader;

    @Value("${jwt.token-prefix}")
    private String tokenPrefix;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = authService.login(loginDTO);
        return Result.success(loginVO);
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterDTO registerDTO) {
        return Result.success(authService.register(registerDTO));
    }

    /**
     * 重置密码
     */
    @PostMapping("/reset-password")
    public Result<String> resetPassword(@Valid @RequestBody ResetPasswordDTO resetPasswordDTO) {
        return Result.success(authService.resetPassword(resetPasswordDTO));
    }

    /**
     * 发送验证码（当前为占位实现）
     */
    @PostMapping("/sendCode")
    public Result<String> sendCode(@RequestBody(required = false) Map<String, Object> body) {
        String phone = body == null ? null : String.valueOf(body.getOrDefault("phone", ""));
        String type = body == null ? null : String.valueOf(body.getOrDefault("type", ""));
        authService.sendCode(phone, type);
        return Result.success("验证码发送成功");
    }

    /**
     * 刷新Token
     */
    @PostMapping("/refresh-token")
    public Result<Map<String, String>> refreshToken(@RequestBody Map<String, String> body) {
        String refreshToken = body == null ? null : body.get("refreshToken");
        if (refreshToken == null || refreshToken.isBlank() || !jwtUtils.validateToken(refreshToken)) {
            throw new BusinessException(401, "refreshToken 无效或已过期");
        }

        Long userId = jwtUtils.getUserIdFromToken(refreshToken);
        String username = jwtUtils.getUsernameFromToken(refreshToken);
        String role = jwtUtils.getRoleFromToken(refreshToken);
        String token = jwtUtils.generateToken(userId, username, role);
        return Result.success(Map.of("token", token));
    }

    /**
     * 退出登录（前后端分离场景下由前端清理本地token）
     */
    @PostMapping("/logout")
    public Result<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader(tokenHeader);
        if (authHeader == null || !authHeader.startsWith(tokenPrefix)) {
            return Result.success("退出成功");
        }
        return Result.success("退出成功");
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/userInfo")
    public Result<UserVO> getUserInfo(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        UserVO userVO = authService.getUserInfo(userId, role);
        return Result.success(userVO);
    }
}
