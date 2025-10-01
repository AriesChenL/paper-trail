package com.lynn.papertrail.service;

import com.lynn.papertrail.dto.PaymentRequest;
import com.lynn.papertrail.dto.PaymentResponse;
import com.lynn.papertrail.entity.PaymentOrder;

import java.util.Optional;

/**
 * 支付服务接口
 *
 * @author lynn
 */
public interface PaymentService {
    /**
     * 创建支付订单
     */
    PaymentResponse createPaymentOrder(PaymentRequest request);

    /**
     * 处理支付回调通知
     */
    String handleNotify(String params);

    /**
     * 查询订单状态
     */
    Optional<PaymentOrder> queryOrder(String outTradeNo);

    /**
     * 主动查询订单并更新状态
     */
    PaymentOrder refreshOrderStatus(String outTradeNo);

    /**
     * 根据商户订单号查询订单
     */
    Optional<PaymentOrder> findByOutTradeNo(String outTradeNo);

    /**
     * 根据支付宝交易号查询订单
     */
    Optional<PaymentOrder> findByTradeNo(String tradeNo);
}