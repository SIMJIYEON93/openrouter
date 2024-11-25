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

    @Value("${openrouter.api.key}")
    private String openrouterApiKey;

    @Value("${openrouter.api.url}")
    private String openrouterApiUrl;

    @Bean
    public WebClient webClient() {
        TcpClient tcpClient = TcpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                .doOnConnected(connection -> connection
                        .addHandlerLast(new ReadTimeoutHandler(120))
                        .addHandlerLast(new WriteTimeoutHandler(120)));

        HttpClient httpClient = HttpClient.from(tcpClient)
                .responseTimeout(Duration.ofSeconds(120));

        return WebClient.builder()
                .baseUrl(openrouterApiUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .defaultHeader("Authorization", "Bearer " + openrouterApiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}

