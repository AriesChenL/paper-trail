package com.lynn.papertrail.mapper;

import com.mybatisflex.core.BaseMapper;
import com.lynn.papertrail.entity.PaymentOrder;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

/**
 * 支付订单映射器
 *
 * @author lynn
 */
public interface PaymentOrderMapper extends BaseMapper<PaymentOrder> {

    /**
     * 根据商户订单号查询订单
     */
    PaymentOrder selectByOutTradeNo(@Param("outTradeNo") String outTradeNo);

    /**
     * 根据支付宝交易号查询订单
     */
    PaymentOrder selectByTradeNo(@Param("tradeNo") String tradeNo);
}