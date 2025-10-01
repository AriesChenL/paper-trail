package com.lynn.papertrail.controller;

import com.lynn.papertrail.dto.PaymentRequest;
import com.lynn.papertrail.dto.PaymentResponse;
import com.lynn.papertrail.entity.PaymentOrder;
import com.lynn.papertrail.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import java.util.Optional;

/**
 * 支付控制器
 *
 * @author lynn
 */
@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    /**
     * 创建支付订单
     */
    @PostMapping("/create")
    public ResponseEntity<PaymentResponse> createPayment(@RequestBody PaymentRequest request) {
        log.info("创建支付订单: 主题={}, 金额={}", request.getSubject(), request.getTotalAmount());
        PaymentResponse response = paymentService.createPaymentOrder(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 查询订单状态
     */
    @GetMapping("/query/{outTradeNo}")
    public ResponseEntity<Optional<PaymentOrder>> queryOrder(@PathVariable String outTradeNo) {
        log.info("查询订单状态: 订单号={}", outTradeNo);
        Optional<PaymentOrder> order = paymentService.queryOrder(outTradeNo);
        return ResponseEntity.ok(order);
    }

    /**
     * 主动刷新订单状态
     */
    @PutMapping("/refresh/{outTradeNo}")
    public ResponseEntity<PaymentOrder> refreshOrder(@PathVariable String outTradeNo) {
        log.info("主动刷新订单状态: 订单号={}", outTradeNo);
        try {
            PaymentOrder order = paymentService.refreshOrderStatus(outTradeNo);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            log.error("刷新订单状态失败", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 支付回调通知接口
     */
    @PostMapping("/notify")
    public ResponseEntity<String> handleNotify(@RequestBody String params) {
        log.info("接收到支付回调通知");
        String result = paymentService.handleNotify(params);
        return ResponseEntity.ok(result);
    }

    /**
     * 支付同步返回接口
     */
    @GetMapping("/return")
    public ResponseEntity<String> handleReturn(@RequestParam Map<String, String> params) {
        log.info("接收到支付同步返回");
        // 这里可以处理同步返回的逻辑
        return ResponseEntity.ok("支付结果处理完成");
    }
}