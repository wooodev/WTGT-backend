package com.swyp.catsgotogedog.common.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class PerspectiveApiConfig {

    @Value("${google.perspective.api.url}")
    private String url;

    @Value("${google.perspective.api.key}")
    private String apiKey;

    private final double nicknameThreshold = 0.6;
    private final double petNameThreshold = 0.8;

}
