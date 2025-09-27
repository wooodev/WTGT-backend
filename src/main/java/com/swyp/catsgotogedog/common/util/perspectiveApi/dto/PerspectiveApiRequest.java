package com.swyp.catsgotogedog.common.util.perspectiveApi.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class PerspectiveApiRequest {

    private Map<String, String> comment;
    private Map<String, Object> requestedAttributes;

    public PerspectiveApiRequest(String text) {
        this.comment = Map.of("text", text);
        this.requestedAttributes = Map.of("TOXICITY", Map.of());
    }
}
