package com.lynn.papertrail.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 支付响应 DTO
 *
 * @author lynn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    /**
     * 商户订单号
     */
    private String outTradeNo;

    /**
     * 订单状态
     */
    private String status;

    /**
     * 支付宝交易号
     */
    private String tradeNo;

    /**
     * 支付页面表单
     */
    private String form;

    /**
     * 错误信息
     */
    private String error;

    /**
     * 响应是否成功
     */
    private Boolean success;
}