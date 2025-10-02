package com.lynn.papertrail.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  实体类。
 *
 * @author lynn
 * @since 2025-10-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("user")
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    private String username;

    private String email;

    private String password;

    private String phone;

    private String nickname;

    /**
     * URL
     */
    private String avatar;

    /**
     * 0-1-2-
     */
    private Integer gender;

    private Integer age;

    /**
     * 0-1-
     */
    private Integer status;

    /**
     * USER-ADMIN-
     */
    private String role;

    private String inviteCode;

    /**
     * ID
     */
    private Long invitedBy;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private LocalDateTime lastLoginTime;

    /**
     * IP
     */
    private String lastLoginIp;

}
