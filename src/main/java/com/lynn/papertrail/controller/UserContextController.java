package com.lynn.papertrail.controller;

import com.lynn.papertrail.service.UserService;
import com.lynn.papertrail.util.UserContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 控制器，用于演示UserContext在存储和检索用户信息方面的用法。
 */
@RestController
@RequestMapping("/api/context")
public class UserContextController {

    @Autowired
    private UserService userService;

    /**
     * 通过在上下文中存储用户信息来模拟用户认证
     */
    @PostMapping("/login")
    public String login(@RequestParam Long userId, 
                        @RequestParam String username, 
                        @RequestParam String role, 
                        @RequestParam String email) {
        
        userService.authenticateUser(userId, username, role, email);
        return "用户已认证，上下文为: " + username;
    }

    /**
     * 获取当前用户上下文信息
     */
    @GetMapping("/current-user")
    public String getCurrentUser() {
        Long userId = UserContextHolder.getUserId();
        String username = UserContextHolder.getUsername();
        String role = UserContextHolder.getUserRole();
        String email = UserContextHolder.getUserEmail();

        StringBuilder response = new StringBuilder();
        response.append("当前用户上下文:\n");
        response.append("  用户ID: ").append(userId != null ? userId : "N/A").append("\n");
        response.append("  用户名: ").append(username != null ? username : "N/A").append("\n");
        response.append("  角色: ").append(role != null ? role : "N/A").append("\n");
        response.append("  邮箱: ").append(email != null ? email : "N/A").append("\n");

        return response.toString();
    }

    /**
     * 使用上下文信息获取当前用户的问候语
     */
    @GetMapping("/greeting")
    public String getGreeting() {
        return userService.getCurrentUserGreeting();
    }

    /**
     * 通过清除上下文来注销当前用户
     */
    @PostMapping("/logout")
    public String logout() {
        userService.logout();
        return "用户已注销，上下文已清除";
    }

    /**
     * 在用户上下文中设置自定义属性
     */
    @PostMapping("/attribute")
    public String setAttribute(@RequestParam String key, @RequestParam String value) {
        UserContextHolder.setAttribute(key, value);
        return "属性 '" + key + "' 设置为 '" + value + "'";
    }

    /**
     * 从用户上下文中获取自定义属性
     */
    @GetMapping("/attribute/{key}")
    public String getAttribute(@PathVariable String key) {
        String value = UserContextHolder.getAttribute(key);
        return value != null ? value : "属性 '" + key + "' 未找到";
    }
}