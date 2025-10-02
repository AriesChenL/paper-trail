package com.lynn.papertrail.service;

import com.lynn.papertrail.util.UserContextHolder;
import org.springframework.stereotype.Service;

/**
 * 示例服务，演示如何使用UserContext存储和检索用户信息。
 */
@Service
public class UserService {

    /**
     * 模拟用户认证并将用户信息存储在上下文中
     * @param userId 用户ID
     * @param username 用户名
     * @param role 用户角色
     * @param email 用户邮箱
     */
    public void authenticateUser(Long userId, String username, String role, String email) {
        UserContextHolder.setUserId(userId);
        UserContextHolder.setUsername(username);
        UserContextHolder.setUserRole(role);
        UserContextHolder.setUserEmail(email);
    }

    /**
     * 从上下文中获取当前认证的用户ID
     * @return 用户ID，如果未认证则返回null
     */
    public Long getCurrentUserId() {
        return UserContextHolder.getUserId();
    }

    /**
     * 从上下文中获取当前认证的用户名
     * @return 用户名，如果未认证则返回null
     */
    public String getCurrentUsername() {
        return UserContextHolder.getUsername();
    }

    /**
     * 从上下文中获取当前用户角色
     * @return 用户角色，如果未认证则返回null
     */
    public String getCurrentUserRole() {
        return UserContextHolder.getUserRole();
    }

    /**
     * 从上下文中获取当前用户邮箱
     * @return 用户邮箱，如果未认证则返回null
     */
    public String getCurrentUserEmail() {
        return UserContextHolder.getUserEmail();
    }

    /**
     * 通过清除上下文来注销当前用户
     */
    public void logout() {
        UserContextHolder.clear();
    }

    /**
     * 模拟一个需要当前用户上下文的操作
     * @return 使用当前用户上下文的问候消息
     */
    public String getCurrentUserGreeting() {
        String username = UserContextHolder.getUsername();
        String role = UserContextHolder.getUserRole();
        
        if (username != null) {
            return "Hello " + username + " (Role: " + (role != null ? role : "unknown") + "), welcome back!";
        }
        
        return "Hello guest, please log in.";
    }
}