package com.lynn.papertrail.service.impl;

import com.lynn.papertrail.dto.UserLoginRequest;
import com.lynn.papertrail.dto.UserLoginResponse;
import com.lynn.papertrail.dto.UserRegisterRequest;
import com.lynn.papertrail.entity.User;
import com.lynn.papertrail.mapper.UserMapper;
import com.lynn.papertrail.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;



/**
 * 用户服务实现类
 *
 * @author lynn
 * @since 2025-10-02
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User register(UserRegisterRequest request) {
        // 检查用户名是否已存在
        User existingUser = userMapper.selectByUsername(request.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (request.getEmail() != null) {
            existingUser = userMapper.selectByEmail(request.getEmail());
            if (existingUser != null) {
                throw new RuntimeException("邮箱已被注册");
            }
        }

        // 创建新用户
        User user = User.builder()
                .username(request.getUsername())
                .password(encryptPassword(request.getPassword()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .nickname(request.getUsername()) // 默认昵称为用户名
                .status(1) // 默认启用
                .role("USER") // 默认为普通用户
                .createTime(LocalDateTime.now())
                .updateTime(LocalDateTime.now())
                .build();

        userMapper.insert(user);
        return user;
    }

    @Override
    public UserLoginResponse login(UserLoginRequest request) {
        User user = userMapper.selectByUsername(request.getUsername());

        if (user == null || !verifyPassword(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getStatus() == 0) {
            throw new RuntimeException("账户已被禁用");
        }

        // 生成UUID作为token
        String token = UUID.randomUUID().toString();

        return new UserLoginResponse(token, user);
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.selectByUsername(username);
    }

    @Override
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User findByUsernameAndPassword(String username, String password) {
        User user = userMapper.selectByUsername(username);
        
        if (user != null && verifyPassword(password, user.getPassword())) {
            return user;
        }
        
        return null;
    }

    /**
     * 加密密码
     */
    private String encryptPassword(String password) {
        // 使用Spring Security的BCrypt加密
        return passwordEncoder.encode(password);
    }

    /**
     * 验证密码
     */
    private boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}