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
    private List<String> stop = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }
}