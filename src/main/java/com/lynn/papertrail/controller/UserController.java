package com.lynn.papertrail.controller;

import com.lynn.papertrail.dto.UserLoginRequest;
import com.lynn.papertrail.dto.UserLoginResponse;
import com.lynn.papertrail.dto.UserRegisterRequest;
import com.lynn.papertrail.entity.User;
import com.lynn.papertrail.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 用户控制器
 *
 * @author lynn
 * @since 2025-10-02
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRegisterRequest request) {
        User user = userService.register(request);
        // 不返回密码字段
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody UserLoginRequest request, HttpServletRequest httpRequest) {
        String clientIp = getClientIpAddress(httpRequest);
        UserLoginResponse response = userService.login(request);
        // 不返回用户密码
        response.getUser().setPassword(null);
        return ResponseEntity.ok(response);
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(@RequestParam String token) {
        // 在实际应用中，这里应该验证token的有效性并从中获取用户ID
        // 为了简单起见，这里我们直接返回一个示例用户（实际中应从数据库中获取）
        // 这里应该通过token从缓存或数据库中查询用户信息
        // 现在我们返回一个假的用户对象用于演示
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setNickname("测试用户");
        user.setEmail("test@example.com");
        user.setCreateTime(java.time.LocalDateTime.now());
        user.setPassword(null); // 不返回密码
        return ResponseEntity.ok(user);
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String token) {
        // 在实际应用中，这里应该将token加入黑名单
        return ResponseEntity.ok("登出成功");
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}