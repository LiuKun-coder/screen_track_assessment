package org.example.sc_backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.sc_backend.common.exception.BusinessException;
import org.example.sc_backend.dto.UserQueryDTO;
import org.example.sc_backend.dto.UserVO;
import org.example.sc_backend.entity.SysUser;
import org.example.sc_backend.mapper.SysUserMapper;
import org.example.sc_backend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public IPage<UserVO> getUserPage(UserQueryDTO queryDTO) {
        Page<SysUser> page = new Page<>(queryDTO.getPage(), queryDTO.getPageSize());
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getDeleted, 0);

        if (StringUtils.hasText(queryDTO.getUserType())) {
            wrapper.eq(SysUser::getUserType, queryDTO.getUserType());
        }
        if (StringUtils.hasText(queryDTO.getStatus())) {
            wrapper.eq(SysUser::getStatus, queryDTO.getStatus());
        }
        if (StringUtils.hasText(queryDTO.getKeyword())) {
            wrapper.and(w -> w
                    .like(SysUser::getUsername, queryDTO.getKeyword())
                    .or()
                    .like(SysUser::getName, queryDTO.getKeyword())
                    .or()
                    .like(SysUser::getPhone, queryDTO.getKeyword())
            );
        }

        wrapper.orderByDesc(SysUser::getCreateTime);
        IPage<SysUser> result = userMapper.selectPage(page, wrapper);
        return result.convert(this::toVO);
    }

    @Override
    public UserVO getUserDetail(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null || user.getDeleted() != null && user.getDeleted() == 1) {
            throw new BusinessException(404, "用户不存在");
        }
        return toVO(user);
    }

    @Override
    public void updateUser(Long id, SysUser updateData) {
        SysUser user = userMapper.selectById(id);
        if (user == null || user.getDeleted() != null && user.getDeleted() == 1) {
            throw new BusinessException(404, "用户不存在");
        }

        user.setName(updateData.getName());
        user.setPhone(updateData.getPhone());
        user.setEmail(updateData.getEmail());
        user.setAvatar(updateData.getAvatar());
        user.setDepartment(updateData.getDepartment());
        user.setUserType(updateData.getUserType());
        if (StringUtils.hasText(updateData.getStatus())) {
            user.setStatus(updateData.getStatus());
        }

        userMapper.updateById(user);
    }

    @Override
    public void updateUserStatus(Long id, String status) {
        if (!"active".equals(status) && !"disabled".equals(status)) {
            throw new BusinessException(400, "状态参数非法");
        }

        SysUser user = userMapper.selectById(id);
        if (user == null || user.getDeleted() != null && user.getDeleted() == 1) {
            throw new BusinessException(404, "用户不存在");
        }

        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public List<UserVO> searchUsers(String keyword) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getDeleted, 0)
                .eq(SysUser::getStatus, "active")
                .select(SysUser::getId, SysUser::getUsername, SysUser::getName, SysUser::getPhone, SysUser::getUserType);

        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w
                    .like(SysUser::getUsername, keyword)
                    .or()
                    .like(SysUser::getName, keyword)
                    .or()
                    .like(SysUser::getPhone, keyword)
            );
        }

        wrapper.orderByDesc(SysUser::getCreateTime).last("limit 20");

        return userMapper.selectList(wrapper)
                .stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateProfile(Long userId, SysUser updateData) {
        SysUser user = userMapper.selectById(userId);
        if (user == null || user.getDeleted() != null && user.getDeleted() == 1) {
            throw new BusinessException(404, "用户不存在");
        }

        if (StringUtils.hasText(updateData.getName())) {
            user.setName(updateData.getName());
        }
        if (StringUtils.hasText(updateData.getPhone())) {
            user.setPhone(updateData.getPhone());
        }
        if (StringUtils.hasText(updateData.getEmail())) {
            user.setEmail(updateData.getEmail());
        }
        if (StringUtils.hasText(updateData.getAvatar())) {
            user.setAvatar(updateData.getAvatar());
        }
        if (StringUtils.hasText(updateData.getDepartment())) {
            user.setDepartment(updateData.getDepartment());
        }

        userMapper.updateById(user);
    }

    private UserVO toVO(SysUser user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        vo.setRole("user");
        return vo;
    }
}
