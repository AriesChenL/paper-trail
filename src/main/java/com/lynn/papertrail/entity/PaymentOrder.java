package com.lynn.papertrail.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付订单实体
 *
 * @author lynn
 */
@Table(value = "payment_order")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentOrder {

    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     *
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 支付宝交易号
     */
    private String tradeNo;

    /**
     * 订单标题
     */
    private String subject;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 订单状态：WAIT_BUYER_PAY(等待支付)、TRADE_SUCCESS(支付成功)、TRADE_CLOSED(交易关闭)
     */
    private String status;

    /**
     * 买家支付宝用户号
     */
    private String buyerId;

    /**
     * 买家支付宝账号
     */
    private String buyerLogonId;

    /**
     * 订单创建时间
     */
    private LocalDateTime createTime;

    /**
     * 订单修改时间
     */
    private LocalDateTime updateTime;

    /**
     * 订单过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 支付完成时间
     */
    private LocalDateTime paymentTime;

    /**
     * 订单描述
     */
    private String body;
}