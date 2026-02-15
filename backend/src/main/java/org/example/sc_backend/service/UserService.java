package org.example.sc_backend.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.sc_backend.dto.UserQueryDTO;
import org.example.sc_backend.dto.UserVO;
import org.example.sc_backend.entity.SysUser;

import java.util.List;

public interface UserService {
    IPage<UserVO> getUserPage(UserQueryDTO queryDTO);

    UserVO getUserDetail(Long id);

    void updateUser(Long id, SysUser updateData);

    void updateUserStatus(Long id, String status);

    List<UserVO> searchUsers(String keyword);

    void updateProfile(Long userId, SysUser updateData);
}
