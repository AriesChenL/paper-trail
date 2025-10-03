package com.lynn.papertrail.service;

import com.lynn.papertrail.dto.UserLoginRequest;
import com.lynn.papertrail.dto.UserLoginResponse;
import com.lynn.papertrail.dto.UserRegisterRequest;
import com.lynn.papertrail.entity.User;

/**
 * 用户服务接口
 *
 * @author lynn
 */
public interface UserService {

    /**
     * 用户注册
     */
    User register(UserRegisterRequest request);

    /**
     * 用户登录
     */
    UserLoginResponse login(UserLoginRequest request);

    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);

    /**
     * 根据ID查找用户
     */
    User findById(Long id);

    /**
     * 根据用户名和密码查找用户
     */
    User findByUsernameAndPassword(String username, String password);
}