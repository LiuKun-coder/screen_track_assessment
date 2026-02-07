package org.example.sc_backend.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.example.sc_backend.common.utils.Result;
import org.example.sc_backend.dto.LoginDTO;
import org.example.sc_backend.dto.LoginVO;
import org.example.sc_backend.dto.UserVO;
import org.example.sc_backend.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO loginDTO) {
        LoginVO loginVO = authService.login(loginDTO);
        return Result.success(loginVO);
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
