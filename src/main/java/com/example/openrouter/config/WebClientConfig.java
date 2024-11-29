package com.example.openrouter.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(WebClientConfig.class);

    @Value("${openrouter.api.key}")
    private String openrouterApiKey;

    @Value("${openrouter.api.url}")
    private String openrouterApiUrl;

    @Bean
    public WebClient webClient() {
        // 환경 변수 유효성 검증
        if (openrouterApiKey == null || openrouterApiKey.isBlank()) {
            logger.warn("OpenRouter API Key is not set. Check your environment variables.");
        }
        if (openrouterApiUrl == null || openrouterApiUrl.isBlank()) {
            logger.warn("OpenRouter API URL is not set. Check your environment variables.");
        }

        // TCP 및 HTTP 클라이언트 설정
        TcpClient tcpClient = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) // 연결 타임아웃 10초
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(120)) // 읽기 타임아웃 120초
                        .addHandlerLast(new WriteTimeoutHandler(120))); // 쓰기 타임아웃 120초

        HttpClient httpClient = HttpClient.from(tcpClient)
                .responseTimeout(Duration.ofSeconds(120)); // 응답 타임아웃 120초

        // WebClient 생성
        return WebClient.builder()
                .baseUrl(openrouterApiUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader("Authorization", "Bearer " + openrouterApiKey) // 인증 헤더
                .defaultHeader("Content-Type", "application/json") // 기본 Content-Type
                .defaultHeader("Accept", "text/event-stream") // 스트리밍 요청을 위한 Accept 헤더
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024)) // 최대 메모리 16MB 설정
                        .build())
                .build();
    }
}

