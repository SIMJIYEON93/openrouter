package com.example.openrouter.aiapi;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

@Service
public class AiApiService {

    private static final Logger logger = LoggerFactory.getLogger(AiApiService.class);
    private final WebClient webClient;

    public AiApiService(@Qualifier("openRouterWebClient")WebClient webClient) {
        if (webClient == null) {
            logger.error("WebClient is null!");
        } else {
            logger.info("WebClient initialized successfully.");
        }
        this.webClient = webClient;
    }

    /**
     * Translate method with additional parameters for temperature and topP.
     */
    public Mono<String> translate(String prompt, Double temperature, Double topP) {
        // TranslationRequest 객체 생성
        TranslationRequest request = new TranslationRequest(prompt, temperature, topP);
        Instant start = Instant.now();


        // jsonPayload 변수 선언
        String jsonPayload = null;
        try {
            jsonPayload = new ObjectMapper().writeValueAsString(request.toApiRequest());
            logger.info("Request Payload: {}", jsonPayload); // JSON 디버깅용
        } catch (Exception e) {
            logger.error("Error serializing request payload: {}", e.getMessage());
            return Mono.error(e);
        }

        // WebClient를 사용해 요청 전송
        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(request.toApiRequest()) // 직렬화된 요청 본문
                .retrieve()
                .bodyToMono(String.class)
                .doOnSubscribe(subscription -> logger.info("translate() request started at: {}", start))
                .doOnSuccess(response -> {
                    Instant end = Instant.now();
                    logger.info("translate() request completed at: {}", end);
                    logger.info("translate() took: {} ms", Duration.between(start, end).toMillis());
                })
                .doOnError(error -> logger.error("Error occurred: {}", error.getMessage()));
    }

    /**
     * Stream-based translation method with additional parameters for temperature and topP.
     */
    public Flux<String> translateStream(String prompt, Double temperature, Double topP) {
        // TranslationRequest 객체 생성
        TranslationRequest request = new TranslationRequest(prompt, temperature, topP);
        Instant start = Instant.now();

        // 요청 페이로드 로깅
        String jsonPayload = null; // 블록 외부에서 선언
        try {
            jsonPayload = new ObjectMapper().writeValueAsString(request.toApiRequest());
            logger.info("Request Payload: {}", jsonPayload); // 디버깅용
        } catch (Exception e) {
            logger.error("Error serializing request payload: {}", e.getMessage());
            return Flux.error(e); // 또는 Mono.error(e)
        }


        // WebClient를 사용해 스트리밍 요청 전송
        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(request.toApiRequest())
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(response -> logger.info("Received chunk: {}", response))
                .doOnError(error -> logger.error("Error occurred: {}", error.getMessage()))
                .doOnSubscribe(subscription -> logger.info("translateStream() request started at: {}", start))
                .doOnComplete(() -> {
                    Instant end = Instant.now();
                    logger.info("translateStream() request completed at: {}", end);
                    logger.info("translateStream() took: {} ms", Duration.between(start, end).toMillis());
                });
    }
}
