package com.lynn.papertrail.dto;

import lombok.Data;

/**
 * 用户信息响应DTO
 *
 * @author lynn
 * @since 2025-10-02
 */
@Data
public class UserInfoResponse {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String phone;
    private String avatar;
    private Integer gender;
    private Integer age;
    private String role;
    private String inviteCode;
    private java.time.LocalDateTime createTime;
}