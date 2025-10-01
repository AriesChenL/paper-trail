package com.lynn.papertrail.mapper;

import com.mybatisflex.core.BaseMapper;
import com.lynn.papertrail.entity.PaymentOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 *  映射层。
 *
 * @author lynn
 * @since 2025-10-01
 */
@Mapper
public interface PaymentOrderMapper extends BaseMapper<PaymentOrder> {

    PaymentOrder selectByOutTradeNo(@Param("outTradeNo") String outTradeNo);

    PaymentOrder selectByTradeNo(@Param("tradeNo") String tradeNo);
}
