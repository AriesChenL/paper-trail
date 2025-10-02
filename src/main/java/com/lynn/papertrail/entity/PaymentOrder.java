package com.lynn.papertrail.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.io.Serial;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付订单表 实体类。
 *
 * @author lynn
 * @since 2025-10-02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("payment_order")
public class PaymentOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /**
     * 订单描述
     */
    private String body;

    /**
     * 买家ID
     */
    private String buyerId;

    /**
     * 买家登录ID
     */
    private String buyerLogonId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 订单标题
     */
    private String subject;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 支付宝交易号
     */
    private String tradeNo;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

}
