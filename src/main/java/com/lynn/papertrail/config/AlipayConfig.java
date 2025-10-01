package com.lynn.papertrail.config;

import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Alipay 配置类
 *
 * @author lynn
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "alipay")
@Slf4j
public class AlipayConfig {

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * 支付宝公钥
     */
    private String publicKey;

    /**
     * 网关地址
     */
    private String gatewayUrl;

    /**
     * 同步通知地址
     */
    private String returnUrl;

    /**
     * 异步通知地址
     */
    private String notifyUrl;

    @Bean
    public AlipayClient alipayClient() {
        log.info("Initializing Alipay client with gateway: {}", gatewayUrl);
        return new DefaultAlipayClient(gatewayUrl, appId, privateKey, "json", "UTF-8", publicKey);
    }
}