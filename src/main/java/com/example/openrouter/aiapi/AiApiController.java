package com.example.openrouter.aiapi;


import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/openai")
public class AiApiController {
    private final AiApiService aiApiService;

    public AiApiController(AiApiService aiApiService) {
        this.aiApiService = aiApiService;
    }

    @PostMapping("/translate")
    public Mono<String> translate(@RequestBody TranslationRequest request) {
        if (request.getPrompt() == null || request.getPrompt().isEmpty()) {
            return Mono.error(new IllegalArgumentException("Prompt must not be null or empty."));
        }
        if (request.getMaxTokens() <= 0) {
            return Mono.error(new IllegalArgumentException("MaxTokens must be greater than 0."));
        }

        return aiApiService.translate(request.getPrompt(), request.getMaxTokens());
    }

    @PostMapping("/translate/stream")
    public Flux<String> translateStream(@RequestBody TranslationRequest request) {
        if (request.getPrompt() == null || request.getPrompt().isEmpty()) {
            return Flux.error(new IllegalArgumentException("Prompt must not be null or empty."));
        }
        if (request.getMaxTokens() <= 0) {
            return Flux.error(new IllegalArgumentException("MaxTokens must be greater than 0."));
        }

        return aiApiService.translateStream(request.getPrompt(), request.getMaxTokens());
    }
}


