package com.lynn.papertrail.service.impl;

import com.alipay.api.AlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.lynn.papertrail.config.AlipayConfig;
import com.lynn.papertrail.dto.PaymentRequest;
import com.lynn.papertrail.dto.PaymentResponse;
import com.lynn.papertrail.entity.PaymentOrder;
import com.lynn.papertrail.mapper.PaymentOrderMapper;
import com.lynn.papertrail.service.PaymentService;
import com.lynn.papertrail.util.PaymentUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 支付服务实现类
 *
 * @author lynn
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final AlipayClient alipayClient;
    private final AlipayConfig alipayConfig;
    private final PaymentOrderMapper paymentOrderMapper;

    @Override
    public PaymentResponse createPaymentOrder(PaymentRequest request) {
        try {
            // 生成商户订单号
            String outTradeNo = "ORDER_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);

            // 创建支付订单实体
            PaymentOrder order = PaymentOrder.builder()
                    .outTradeNo(outTradeNo)
                    .subject(request.getSubject())
                    .totalAmount(request.getTotalAmount())
                    .status("WAIT_BUYER_PAY")
                    .body(request.getBody())
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .expireTime(LocalDateTime.now().plusMinutes(request.getExpireTime()))
                    .build();

            // 保存订单
            paymentOrderMapper.insert(order);
            order = paymentOrderMapper.selectByOutTradeNo(outTradeNo);

            // 构建支付宝支付请求
            AlipayTradePagePayRequest payRequest = new AlipayTradePagePayRequest();
            payRequest.setReturnUrl(alipayConfig.getReturnUrl());
            payRequest.setNotifyUrl(alipayConfig.getNotifyUrl());

            // 构建业务参数
            Map<String, Object> bizContent = Map.of(
                    "out_trade_no", outTradeNo,
                    "total_amount", request.getTotalAmount().toString(),
                    "subject", request.getSubject(),
                    "body", request.getBody(),
                    "product_code", "FAST_INSTANT_TRADE_PAY",
                    "time_expire", request.getExpireTime() + "m"
            );
            payRequest.setBizContent(mapToJson(bizContent));

            // 执行请求
            AlipayTradePagePayResponse response = alipayClient.pageExecute(payRequest);

            if (response.isSuccess()) {
                log.info("支付订单创建成功: 订单号={}, 订单金额={}", outTradeNo, request.getTotalAmount());
                return PaymentResponse.builder()
                        .outTradeNo(outTradeNo)
                        .status("WAIT_BUYER_PAY")
                        .form(response.getBody())
                        .success(true)
                        .build();
            } else {
                log.error("支付订单创建失败: 错误码={}, 错误信息={}", response.getCode(), response.getMsg());
                return PaymentResponse.builder()
                        .outTradeNo(outTradeNo)
                        .status("CREATE_FAILED")
                        .error("支付订单创建失败: " + response.getMsg())
                        .success(false)
                        .build();
            }
        } catch (Exception e) {
            log.error("创建支付订单时发生异常", e);
            return PaymentResponse.builder()
                    .status("CREATE_FAILED")
                    .error("创建支付订单时发生异常: " + e.getMessage())
                    .success(false)
                    .build();
        }
    }

    @Override
    public String handleNotify(String params) {
        try {
            // 验签
            Map<String, String> paramsMap = PaymentUtils.parseQueryString(params);
            boolean signVerified = AlipaySignature.rsaCheckV1(
                    paramsMap,
                    alipayConfig.getPublicKey(),
                    "UTF-8"
            );

            if (!signVerified) {
                log.error("支付回调验签失败");
                return "failure";
            }

            // 获取参数
            String outTradeNo = paramsMap.get("out_trade_no");
            String tradeNo = paramsMap.get("trade_no");
            String tradeStatus = paramsMap.get("trade_status");
            String buyerId = paramsMap.get("buyer_id");
            String buyerLogonId = paramsMap.get("buyer_logon_id");

            // 更新订单状态
            PaymentOrder order = paymentOrderMapper.selectByOutTradeNo(outTradeNo);
            if (order != null) {

                // 根据支付宝状态更新订单状态
                switch (tradeStatus) {
                    case "TRADE_SUCCESS":
                    case "TRADE_FINISHED":
                        order.setStatus("TRADE_SUCCESS");
                        order.setTradeNo(tradeNo);
                        order.setBuyerId(buyerId);
                        order.setBuyerLogonId(buyerLogonId);
                        order.setPaymentTime(LocalDateTime.now());
                        break;
                    case "TRADE_CLOSED":
                        order.setStatus("TRADE_CLOSED");
                        break;
                    default:
                        log.warn("接收到未处理的交易状态: {}", tradeStatus);
                        break;
                }

                // First get the original order to get the ID
                PaymentOrder originalOrder = paymentOrderMapper.selectByOutTradeNo(outTradeNo);
                if (originalOrder != null) {
                    // Update the original order with new values
                    originalOrder.setStatus(order.getStatus());
                    originalOrder.setTradeNo(order.getTradeNo());
                    originalOrder.setBuyerId(order.getBuyerId());
                    originalOrder.setBuyerLogonId(order.getBuyerLogonId());
                    originalOrder.setPaymentTime(order.getPaymentTime());

                    // Update by ID since we have the original order with ID
                    int updated = paymentOrderMapper.update(originalOrder);
                    log.info("支付回调处理成功: 订单号={}, 交易状态={}, 更新记录数={}", outTradeNo, tradeStatus, updated);
                } else {
                    log.error("未找到对应的订单: 商户订单号={}", outTradeNo);
                    return "failure";
                }
            } else {
                log.error("未找到对应的订单: 商户订单号={}", outTradeNo);
                return "failure";
            }

            return "success";
        } catch (Exception e) {
            log.error("处理支付回调时发生异常", e);
            return "failure";
        }
    }

    @Override
    public Optional<PaymentOrder> queryOrder(String outTradeNo) {
        PaymentOrder order = paymentOrderMapper.selectByOutTradeNo(outTradeNo);
        return Optional.ofNullable(order);
    }

    @Override
    public PaymentOrder refreshOrderStatus(String outTradeNo) {
        try {
            PaymentOrder order = paymentOrderMapper.selectByOutTradeNo(outTradeNo);
            if (order == null) {
                throw new RuntimeException("订单不存在: " + outTradeNo);
            }

            // 构建查询请求
            AlipayTradeQueryRequest queryRequest = new AlipayTradeQueryRequest();
            Map<String, Object> bizContent = Map.of(
                    "out_trade_no", outTradeNo
            );
            queryRequest.setBizContent(mapToJson(bizContent));

            // 执行查询
            AlipayTradeQueryResponse response = alipayClient.certificateExecute(queryRequest);

            if (response.isSuccess()) {
                String tradeStatus = response.getTradeStatus();

                // 更新订单状态
                switch (tradeStatus) {
                    case "TRADE_SUCCESS":
                    case "TRADE_FINISHED":
                        order.setStatus("TRADE_SUCCESS");
                        if (order.getTradeNo() == null) {
                            order.setTradeNo(response.getTradeNo());
                        }
                        if (order.getBuyerId() == null) {
                            order.setBuyerId(response.getBuyerUserId());
                        }
                        if (order.getBuyerLogonId() == null) {
                            order.setBuyerLogonId(response.getBuyerLogonId());
                        }
                        if (order.getPaymentTime() == null) {
                            order.setPaymentTime(LocalDateTime.now());
                        }
                        break;
                    case "TRADE_CLOSED":
                        order.setStatus("TRADE_CLOSED");
                        break;
                    case "WAIT_BUYER_PAY":
                        order.setStatus("WAIT_BUYER_PAY");
                        break;
                    default:
                        log.warn("接收到未处理的交易状态: {}", tradeStatus);
                        break;
                }

                // First get the original order to get the ID
                PaymentOrder originalOrder = paymentOrderMapper.selectByOutTradeNo(outTradeNo);
                if (originalOrder != null) {
                    // Update the original order with new values
                    originalOrder.setStatus(order.getStatus());
                    originalOrder.setTradeNo(order.getTradeNo());
                    originalOrder.setBuyerId(order.getBuyerId());
                    originalOrder.setBuyerLogonId(order.getBuyerLogonId());
                    originalOrder.setPaymentTime(order.getPaymentTime());

                    // Update by ID since we have the original order with ID
                    int updated = paymentOrderMapper.update(originalOrder);
                    log.info("订单状态刷新成功: 订单号={}, 当前状态={}, 更新记录数={}", outTradeNo, order.getStatus(), updated);
                }
            } else {
                log.error("查询订单状态失败: 订单号={}, 错误码={}, 错误信息={}",
                        outTradeNo, response.getCode(), response.getMsg());
            }

            return order;
        } catch (Exception e) {
            log.error("刷新订单状态时发生异常: 订单号={}", outTradeNo, e);
            throw new RuntimeException("刷新订单状态失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<PaymentOrder> findByOutTradeNo(String outTradeNo) {
        PaymentOrder order = paymentOrderMapper.selectByOutTradeNo(outTradeNo);
        return Optional.ofNullable(order);
    }

    @Override
    public Optional<PaymentOrder> findByTradeNo(String tradeNo) {
        PaymentOrder order = paymentOrderMapper.selectByTradeNo(tradeNo);
        return Optional.ofNullable(order);
    }

    /**
     * 将Map转换为JSON字符串
     */
    private String mapToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof String) {
                json.append("\"").append(entry.getValue()).append("\"");
            } else {
                json.append(entry.getValue());
            }
            first = false;
        }
        json.append("}");
        return json.toString();
    }
}