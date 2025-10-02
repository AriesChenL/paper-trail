-- 还原 payment_order 表的创建语句
CREATE TABLE `payment_order` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `body` VARCHAR(500) DEFAULT NULL COMMENT '订单描述',
    `buyer_id` VARCHAR(64) DEFAULT NULL COMMENT '买家ID',
    `buyer_logon_id` VARCHAR(64) DEFAULT NULL COMMENT '买家登录ID',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间',
    `out_trade_no` VARCHAR(64) NOT NULL COMMENT '商户订单号',
    `payment_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `status` VARCHAR(32) DEFAULT NULL COMMENT '订单状态',
    `subject` VARCHAR(255) DEFAULT NULL COMMENT '订单标题',
    `total_amount` DECIMAL(10, 2) DEFAULT NULL COMMENT '订单总金额',
    `trade_no` VARCHAR(64) DEFAULT NULL COMMENT '支付宝交易号',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    INDEX `idx_out_trade_no` (`out_trade_no`),
    INDEX `idx_trade_no` (`trade_no`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='支付订单表';