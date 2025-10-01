package com.lynn.papertrail.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 支付请求 DTO
 *
 * @author lynn
 */
@Data
public class PaymentRequest {
    /**
     * 订单标题
     */
    private String subject;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单描述
     */
    private String body;

    /**
     * 订单过期时间（分钟）
     */
    private Integer expireTime = 30;
}