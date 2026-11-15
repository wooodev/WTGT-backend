package com.swyp.catsgotogedog.content.domain.request;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class) // maxTokens -> max_tokens
@JsonInclude(JsonInclude.Include.NON_NULL)                     // null 필드는 전송 안 함
public class OpenAiChatRequest {
    private String model;
    private List<Message> messages;
    private Double temperature;
    private Integer maxTokens;
    private List<String> stop;
    private ResponseFormat responseFormat; // 추가: JSON 모드용 (null이면 전송 안 함)

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResponseFormat {
        private String type; // "json_object"

        public static ResponseFormat json() {
            return new ResponseFormat("json_object");
        }
    }
}