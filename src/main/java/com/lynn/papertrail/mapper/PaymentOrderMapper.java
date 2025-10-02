package com.lynn.papertrail.mapper;

import com.mybatisflex.core.BaseMapper;
import com.lynn.papertrail.entity.PaymentOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付订单表 映射层。
 *
 * @author lynn
 * @since 2025-10-02
 */
@Mapper
public interface PaymentOrderMapper extends BaseMapper<PaymentOrder> {

    PaymentOrder selectByOutTradeNo(String outTradeNo);

    PaymentOrder selectByTradeNo(String tradeNo);
}
