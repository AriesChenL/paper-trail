CREATE TABLE `user`
(
    `id`          bigint                                  NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username`    varchar(50) COLLATE utf8mb4_unicode_ci  NOT NULL COMMENT '用户名',
    `email`       varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱',
    `password`    varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码（加密后）',
    `phone`       varchar(20) COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '手机号',
    `nickname`    varchar(50) COLLATE utf8mb4_unicode_ci  DEFAULT NULL COMMENT '昵称',
    `avatar`      varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '头像URL',
    `gender`      tinyint                                 DEFAULT '0' COMMENT '性别：0-未知，1-男，2-女',
    `status`      tinyint                                 DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
    `role`        varchar(20) COLLATE utf8mb4_unicode_ci  DEFAULT 'USER' COMMENT '角色：USER-普通用户，ADMIN-管理员',
    `create_time` datetime                                DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime                                DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `username` (`username`),
    UNIQUE KEY `email` (`email`),
    KEY `idx_username` (`username`),
    KEY `idx_email` (`email`),
    KEY `idx_phone` (`phone`),
    KEY `idx_create_time` (`create_time`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='用户表'

