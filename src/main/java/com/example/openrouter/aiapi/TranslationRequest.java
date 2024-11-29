package com.example.openrouter.aiapi;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.*;

@Data
@AllArgsConstructor
public class TranslationRequest {

        private String prompt;
        private int maxTokens;

        public Map<String, Object> toApiRequest() {
            if (prompt == null || prompt.isEmpty()) {
                throw new IllegalArgumentException("Prompt must not be null or empty.");
            }
            if (maxTokens <= 0) {
                throw new IllegalArgumentException("MaxTokens must be greater than 0.");
            }

            String modifiedPrompt = "다음 내용을 초등학교 3학년이 이해할 수 있도록 쉽게 바꿔주세요:\n" + prompt;

            Map<String, Object> request = new HashMap<>();
            request.put("model", "anthropic.claude-v3.5");

            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of(
                    "role", "user",
                    "content", modifiedPrompt
            ));
            request.put("messages", messages);
            request.put("max_tokens", maxTokens);

            return request;
        }
    }



