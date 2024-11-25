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
        // 유효성 검사 추가
        if (prompt == null || prompt.isEmpty()) {
            throw new IllegalArgumentException("Prompt must not be null or empty.");
        }
        if (maxTokens <= 0) {
            throw new IllegalArgumentException("MaxTokens must be greater than 0.");
        }

        JSONObject request = new JSONObject();
        request.put("model", "gpt-3.5-turbo");

        // 메시지 배열 생성
        JSONArray messages = new JSONArray();

        // 시스템 메시지 추가
        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", "글의 내용을 초등학교3학년이 이해할 수 있는 글로 번안해줘")
        );

        // 사용자 메시지 추가
        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", prompt)
        );

        request.put("messages", messages);
        request.put("max_tokens", maxTokens);
        return request;
    }
}

