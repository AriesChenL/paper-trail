package com.lynn.papertrail.dto;

import com.lynn.papertrail.entity.User;
import lombok.Data;

/**
 * 用户登录响应DTO
 *
 * @author lynn
 * @since 2025-10-02
 */
@Data
public class UserLoginResponse {
    private String token;
    private User user;
    
    public UserLoginResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }
}