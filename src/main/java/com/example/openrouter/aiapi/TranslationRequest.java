package com.example.openrouter.aiapi;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;

@Data
@AllArgsConstructor
public class TranslationRequest {
    private String prompt;
    private int maxTokens;

    public JSONObject toApiRequest() {
        // 유효성 검사
        if (prompt == null || prompt.isEmpty()) {
            throw new IllegalArgumentException("Prompt must not be null or empty.");
        }
        if (maxTokens <= 0) {
            throw new IllegalArgumentException("MaxTokens must be greater than 0.");
        }

        String modifiedPrompt = "다음 내용을 초등학교 3학년이 이해할 수 있도록 쉽게 바꿔주세요:\n"
                + prompt;

        JSONObject request = new JSONObject();
        request.put("model", "anthropic.claude-v2");

        JSONArray messages = new JSONArray();

        // 사용자 메시지 추가
        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", modifiedPrompt)
        );

        request.put("messages", messages);
        request.put("max_tokens", maxTokens);

        return request;
    }
}



