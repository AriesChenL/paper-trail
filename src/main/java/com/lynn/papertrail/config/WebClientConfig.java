package com.lynn.papertrail.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.xml.Jaxb2XmlDecoder;
import org.springframework.http.codec.xml.Jaxb2XmlEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

/**
 * WebClient配置类
 *
 * @author lynn
 */
@Configuration
public class WebClientConfig {

    @Value("${webclient.proxy.host:}")
    private String proxyHost;

    @Value("${webclient.proxy.port:0}")
    private int proxyPort;

    @Bean
    public WebClient webClient() {
        WebClient.Builder builder = WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(this::configureCodecs)
                        .build());

        if (proxyHost != null && !proxyHost.isEmpty() && proxyPort > 0) {
            HttpClient httpClient = HttpClient.create()
                    .proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP)
                            .host(proxyHost)
                            .port(proxyPort));
            builder.clientConnector(new ReactorClientHttpConnector(httpClient));
        }

        return builder.build();
    }

    private void configureCodecs(ClientCodecConfigurer configurer) {
        // 增加内存缓冲区大小以处理较大的XML响应
        configurer.defaultCodecs().maxInMemorySize(1024 * 1024);

        // 添加XML编解码器
        configurer.defaultCodecs().jaxb2Encoder(new Jaxb2XmlEncoder());
        configurer.defaultCodecs().jaxb2Decoder(new Jaxb2XmlDecoder());
    }

    @Bean
    public XmlMapper xmlMapper() {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.registerModule(new JavaTimeModule());
        return xmlMapper;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
