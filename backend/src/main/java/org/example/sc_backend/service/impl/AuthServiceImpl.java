package org.example.sc_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.sc_backend.common.exception.BusinessException;
import org.example.sc_backend.common.utils.JwtUtils;
import org.example.sc_backend.dto.LoginDTO;
import org.example.sc_backend.dto.LoginVO;
import org.example.sc_backend.dto.RegisterDTO;
import org.example.sc_backend.dto.ResetPasswordDTO;
import org.example.sc_backend.dto.UserVO;
import org.example.sc_backend.entity.SysAdmin;
import org.example.sc_backend.entity.SysUser;
import org.example.sc_backend.mapper.SysAdminMapper;
import org.example.sc_backend.mapper.SysUserMapper;
import org.example.sc_backend.service.AuthService;
import org.example.sc_backend.service.SmsCodeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.regex.Pattern;
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

    @Autowired
    private SmsCodeService smsCodeService;

    private static final Pattern BCRYPT_PATTERN = Pattern.compile("\\$2[aby]?\\$\\d{1,2}\\$[./A-Za-z0-9]{53}");

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

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
            if (!verifyPassword(password, admin.getPassword())) {
                throw new BusinessException(401, "密码错误");
            }
            upgradeAdminPasswordIfPlaintext(admin, password);
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

        if (!verifyPassword(password, user.getPassword())) {
            throw new BusinessException(401, "密码错误");
        }

        upgradeUserPasswordIfPlaintext(user, password);

        return buildLoginVO(user);
    }

    @Override
    public String register(RegisterDTO registerDTO) {
        String phone = registerDTO.getPhone();
        String registerCode = firstNonBlank(registerDTO.getVerifyCode(), registerDTO.getCode());
        smsCodeService.validateCode(phone, "register", registerCode);

        String username = firstNonBlank(registerDTO.getUsername(), phone);
        if (username == null || username.isBlank()) {
            throw new BusinessException(400, "用户名不能为空");
        }

        SysUser existUser = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username)
                        .eq(SysUser::getDeleted, 0)
        );
        if (existUser != null) {
            throw new BusinessException(400, "用户名已存在");
        }

        SysAdmin existAdmin = adminMapper.selectOne(
                new LambdaQueryWrapper<SysAdmin>()
                        .eq(SysAdmin::getUsername, username)
                        .eq(SysAdmin::getDeleted, 0)
        );
        if (existAdmin != null) {
            throw new BusinessException(400, "用户名已存在");
        }

        SysUser user = new SysUser();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setName(firstNonBlank(registerDTO.getName(), registerDTO.getNickname(), username));
        user.setPhone(phone);
        user.setEmail(registerDTO.getEmail());
        user.setDepartment(registerDTO.getDepartment());
        user.setUserType(registerDTO.getUserType() == null || registerDTO.getUserType().isBlank() ? "student" : registerDTO.getUserType());
        user.setStatus("active");
        user.setDeleted(0);

        userMapper.insert(user);
        return "注册成功";
    }

    @Override
    public String resetPassword(ResetPasswordDTO resetPasswordDTO) {
        String phone = resetPasswordDTO.getPhone();
        String newPassword = resetPasswordDTO.getNewPassword();
        String resetCode = firstNonBlank(resetPasswordDTO.getVerifyCode(), resetPasswordDTO.getCode());

        smsCodeService.validateCode(phone, "reset", resetCode);

        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getPhone, phone)
                        .eq(SysUser::getDeleted, 0)
        );
        if (user != null) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userMapper.updateById(user);
            return "重置成功";
        }

        SysAdmin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<SysAdmin>()
                        .eq(SysAdmin::getPhone, phone)
                        .eq(SysAdmin::getDeleted, 0)
        );
        if (admin != null) {
            admin.setPassword(passwordEncoder.encode(newPassword));
            adminMapper.updateById(admin);
            return "重置成功";
        }

        throw new BusinessException(404, "该手机号未绑定账号");
    }

    @Override
    public void sendCode(String phone, String type) {
        smsCodeService.sendCode(phone, type);
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

    private boolean verifyPassword(String rawPassword, String storedPassword) {
        if (storedPassword == null) {
            return false;
        }
        if (isBcryptHash(storedPassword)) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }
        return rawPassword.equals(storedPassword);
    }

    private void upgradeUserPasswordIfPlaintext(SysUser user, String rawPassword) {
        if (user == null || user.getPassword() == null) {
            return;
        }
        if (!isBcryptHash(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(rawPassword));
            userMapper.updateById(user);
        }
    }

    private void upgradeAdminPasswordIfPlaintext(SysAdmin admin, String rawPassword) {
        if (admin == null || admin.getPassword() == null) {
            return;
        }
        if (!isBcryptHash(admin.getPassword())) {
            admin.setPassword(passwordEncoder.encode(rawPassword));
            adminMapper.updateById(admin);
        }
    }

    private boolean isBcryptHash(String password) {
        return password.length() >= 60 && BCRYPT_PATTERN.matcher(password).matches();
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
