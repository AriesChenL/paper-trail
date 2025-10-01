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
 *  实体类。
 *
 * @author lynn
 * @since 2025-10-01
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("payment_order")
public class PaymentOrder implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String body;

    private String buyerId;

    private String buyerLogonId;

    private LocalDateTime createTime;

    private LocalDateTime expireTime;

    private String outTradeNo;

    private LocalDateTime paymentTime;

    private String status;

    private String subject;

    private BigDecimal totalAmount;

    private String tradeNo;

    private LocalDateTime updateTime;

}
