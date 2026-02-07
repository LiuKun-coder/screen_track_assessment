package org.example.sc_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.sc_backend.common.exception.BusinessException;
import org.example.sc_backend.common.utils.JwtUtils;
import org.example.sc_backend.dto.LoginDTO;
import org.example.sc_backend.dto.LoginVO;
import org.example.sc_backend.dto.UserVO;
import org.example.sc_backend.entity.SysAdmin;
import org.example.sc_backend.entity.SysUser;
import org.example.sc_backend.mapper.SysAdminMapper;
import org.example.sc_backend.mapper.SysUserMapper;
import org.example.sc_backend.service.AuthService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysAdminMapper adminMapper;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public LoginVO login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();

        // 先查管理员表
        SysAdmin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<SysAdmin>()
                        .eq(SysAdmin::getUsername, username)
                        .eq(SysAdmin::getDeleted, 0)
        );

        if (admin != null) {
            // 验证管理员密码 (实际项目中应使用加密比较)
            if (!password.equals(admin.getPassword())) {
                throw new BusinessException(401, "密码错误");
            }
            return buildLoginVO(admin);
        }

        // 再查普通用户表
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
                        .eq(SysUser::getDeleted, 0)
        );

        if (user == null) {
            throw new BusinessException(401, "用户不存在");
        }

        if (!"active".equals(user.getStatus())) {
            throw new BusinessException(401, "账号已被禁用");
        }

        // 验证用户密码
        if (!password.equals(user.getPassword())) {
            throw new BusinessException(401, "密码错误");
        }

        return buildLoginVO(user);
    }

    @Override
    public UserVO getUserInfo(Long userId, String role) {
        UserVO vo = new UserVO();

        if ("admin".equals(role) || "super_admin".equals(role)) {
            SysAdmin admin = adminMapper.selectById(userId);
            if (admin == null) {
                throw new BusinessException(404, "用户不存在");
            }
            vo.setId(admin.getId());
            vo.setUsername(admin.getUsername());
            vo.setName(admin.getName());
            vo.setPhone(admin.getPhone());
            vo.setDepartment(admin.getDepartment());
            vo.setRole(admin.getRole());
            vo.setPermissions(Arrays.asList("admin", "violation:manage", "appeal:manage", "user:manage"));
        } else {
            SysUser user = userMapper.selectById(userId);
            if (user == null) {
                throw new BusinessException(404, "用户不存在");
            }
            BeanUtils.copyProperties(user, vo);
            vo.setRole("user");
            vo.setPermissions(Arrays.asList("violation:view", "appeal:submit"));
        }

        return vo;
    }

    private LoginVO buildLoginVO(SysAdmin admin) {
        String token = jwtUtils.generateToken(admin.getId(), admin.getUsername(), admin.getRole());
        UserVO userVO = new UserVO();
        userVO.setId(admin.getId());
        userVO.setUsername(admin.getUsername());
        userVO.setName(admin.getName());
        userVO.setPhone(admin.getPhone());
        userVO.setDepartment(admin.getDepartment());
        userVO.setRole(admin.getRole());
        userVO.setPermissions(Arrays.asList("admin", "violation:manage", "appeal:manage", "user:manage"));

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserInfo(userVO);
        return loginVO;
    }

    private LoginVO buildLoginVO(SysUser user) {
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), "user");
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setRole("user");
        userVO.setPermissions(Arrays.asList("violation:view", "appeal:submit"));

        LoginVO loginVO = new LoginVO();
        loginVO.setToken(token);
        loginVO.setUserInfo(userVO);
        return loginVO;
    }
}
