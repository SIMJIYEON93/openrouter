package com.example.openrouter.aiapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public AiApiService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> translate(String prompt, int maxTokens) {
        TranslationRequest request = new TranslationRequest(prompt, maxTokens);
        Instant start = Instant.now();

        return webClient.post()
                .uri("")
                .bodyValue(request.toApiRequest().toString())
                .retrieve()
                .bodyToMono(String.class)
                .doOnSubscribe(subscription -> logger.info("translate() request started at: {}", start))
                .doOnSuccess(response -> {
                    Instant end = Instant.now();
                    logger.info("translate() request completed at: {}", end);
                    logger.info("translate() took: {} ms", Duration.between(start, end).toMillis());
                });
    }

    public Flux<String> translateStream(String prompt, int maxTokens) {
        TranslationRequest request = new TranslationRequest(prompt, maxTokens);
        Instant start = Instant.now();

        return webClient.post()
                .uri("")
                .bodyValue(request.toApiRequest().toString())
                .retrieve()
                .bodyToFlux(String.class)
                .doOnSubscribe(subscription -> logger.info("translateStream() request started at: {}", start))
                .doOnComplete(() -> {
                    Instant end = Instant.now();
                    logger.info("translateStream() request completed at: {}", end);
                    logger.info("translateStream() took: {} ms", Duration.between(start, end).toMillis());
                });
    }
}



