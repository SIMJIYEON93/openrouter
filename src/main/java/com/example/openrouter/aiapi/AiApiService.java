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

    public AiApiService(@Qualifier("openRouterWebClient") WebClient webClient) {
        if (webClient == null) {
            logger.error("WebClient is null!");
        } else {
            logger.info("WebClient initialized successfully.");
        }
        this.webClient = webClient;
    }


    public Mono<String> translate(String prompt, Double temperature, Double topP) {
        TranslationRequest request = new TranslationRequest(prompt, temperature, topP);
        Instant start = Instant.now();
        final boolean[] firstResponseLogged = {false}; // 첫 응답 여부를 추적

        String jsonPayload = null;
        try {
            jsonPayload = new ObjectMapper().writeValueAsString(request.toApiRequest());
            logger.info("Request Payload: {}", jsonPayload);
        } catch (Exception e) {
            logger.error("Error serializing request payload: {}", e.getMessage());
            return Mono.error(e);
        }

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(request.toApiRequest())
                .retrieve()
                .bodyToMono(String.class)
                .doOnSubscribe(subscription -> logger.info("translate() request started at: {}", start))
                .doOnNext(response -> {
                    if (!firstResponseLogged[0]) {
                        firstResponseLogged[0] = true;
                        Instant firstResponseReceived = Instant.now();
                        logger.info("First Response Received at: {}", firstResponseReceived);
                        logger.info("Time to first byte: {} ms",
                                Duration.between(start, firstResponseReceived).toMillis());
                    }
                })
                .doOnSuccess(response -> {
                    Instant end = Instant.now();
                    logger.info("translate() request completed at: {}", end);
                    logger.info("translate() took: {} ms", Duration.between(start, end).toMillis());
                })
                .doOnError(error -> logger.error("Error occurred: {}", error.getMessage()));
    }


    public Flux<String> translateStream(String prompt, Double temperature, Double topP) {
        TranslationRequest request = new TranslationRequest(prompt, temperature, topP);
        Instant start = Instant.now();
        final boolean[] firstResponseLogged = {false}; // 첫 응답 여부를 추적

        String jsonPayload = null;
        try {
            jsonPayload = new ObjectMapper().writeValueAsString(request.toApiRequest());
            logger.info("Request Payload: {}", jsonPayload);
        } catch (Exception e) {
            logger.error("Error serializing request payload: {}", e.getMessage());
            return Flux.error(e);
        }

        return webClient.post()
                .uri("/chat/completions")
                .bodyValue(request.toApiRequest())
                .retrieve()
                .bodyToFlux(String.class)
                .doOnSubscribe(subscription -> logger.info("translateStream() request started at: {}", start))
                .doOnNext(response -> {
                    if (!firstResponseLogged[0]) {
                        firstResponseLogged[0] = true;
                        Instant firstResponseReceived = Instant.now();
                        logger.info("First Response Received at: {}", firstResponseReceived);
                        logger.info("Time to first byte: {} ms",
                                Duration.between(start, firstResponseReceived).toMillis());
                    }
                    logger.info("Received chunk: {}", response);
                })
                .doOnComplete(() -> {
                    Instant end = Instant.now();
                    logger.info("translateStream() request completed at: {}", end);
                    logger.info("translateStream() took: {} ms", Duration.between(start, end).toMillis());
                })
                .doOnError(error -> logger.error("Error occurred: {}", error.getMessage()));
    }
}
