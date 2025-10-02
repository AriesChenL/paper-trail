package com.lynn.papertrail.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * 用户注册请求DTO
 *
 * @author lynn
 * @since 2025-10-02
 */
@Data
public class UserRegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{3,20}$", message = "用户名只能包含字母、数字和下划线，长度3-20位")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{6,20}$", message = "密码至少包含一个字母和一个数字，长度6-20位")
    private String password;

    private String email;

    private String phone;
}