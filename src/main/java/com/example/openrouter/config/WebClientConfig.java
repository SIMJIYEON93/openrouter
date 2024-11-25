package com.example.openrouter.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.time.Duration;

@Configuration
public class WebClientConfig {


    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Bean
    public WebClient webClient() {
        TcpClient tcpClient = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000) // 연결 타임아웃 10초
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(120)) // 읽기 타임아웃 120초
                        .addHandlerLast(new WriteTimeoutHandler(120))); // 쓰기 타임아웃 120초

        HttpClient httpClient = HttpClient.from(tcpClient)
                .responseTimeout(Duration.ofSeconds(120));

        return WebClient.builder()
                .baseUrl("https://api.openai.com/v1") // OpenAI API 기본 URL
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader("Authorization", "Bearer " + openaiApiKey) // OpenAI API Key
                .defaultHeader("Content-Type", "application/json")
                .filter((request, next) -> {
                    System.out.println("Request Method: " + request.method());
                    System.out.println("Request URL: " + request.url());
                    return next.exchange(request)
                            .doOnNext(response -> {
                                System.out.println("Response Status Code: " + response.statusCode());
                            });
                })
                .build();
    }
}
