package org.example.sc_backend.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.servlet.http.HttpServletRequest;
import org.example.sc_backend.common.utils.AdminAuthUtils;
import org.example.sc_backend.common.utils.Result;
import org.example.sc_backend.dto.UserQueryDTO;
import org.example.sc_backend.dto.UserVO;
import org.example.sc_backend.entity.SysUser;
import org.example.sc_backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public Result<IPage<UserVO>> getUserList(HttpServletRequest request, UserQueryDTO queryDTO) {
        AdminAuthUtils.requireAdmin(request);
        return Result.success(userService.getUserPage(queryDTO));
    }

    @GetMapping("/{id}")
    public Result<UserVO> getUserDetail(HttpServletRequest request, @PathVariable Long id) {
        AdminAuthUtils.requireAdmin(request);
        return Result.success(userService.getUserDetail(id));
    }

    @PutMapping("/{id}")
    public Result<Void> updateUser(HttpServletRequest request, @PathVariable Long id, @RequestBody SysUser updateData) {
        AdminAuthUtils.requireAdmin(request);
        userService.updateUser(id, updateData);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(HttpServletRequest request, @PathVariable Long id, @RequestBody Map<String, String> body) {
        AdminAuthUtils.requireAdmin(request);
        userService.updateUserStatus(id, body.get("status"));
        return Result.success();
    }

    @GetMapping("/search")
    public Result<List<UserVO>> searchUsers(HttpServletRequest request, @RequestParam(required = false) String keyword) {
        AdminAuthUtils.requireAdmin(request);
        return Result.success(userService.searchUsers(keyword));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(HttpServletRequest request, @RequestBody SysUser updateData) {
        Long userId = (Long) request.getAttribute("userId");
        userService.updateProfile(userId, updateData);
        return Result.success();
    }
}
