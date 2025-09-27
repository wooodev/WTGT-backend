package com.swyp.catsgotogedog.common.util.perspectiveApi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swyp.catsgotogedog.common.config.PerspectiveApiConfig;
import com.swyp.catsgotogedog.common.util.perspectiveApi.dto.PerspectiveApiRequest;
import com.swyp.catsgotogedog.common.util.perspectiveApi.dto.PerspectiveApiResponse;
import com.swyp.catsgotogedog.global.exception.ErrorCode;
import com.swyp.catsgotogedog.global.exception.PerspectiveApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerspectiveApiService {

    private final PerspectiveApiConfig config;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public double getToxicityScore(String text) {
        // 1. 요청 객체 생성
        PerspectiveApiRequest request = new PerspectiveApiRequest(text);

        // 2. API 호출
        String response = callApi(request);

        // 3. 응답 파싱
        return parseResponse(response);
    }

    private String callApi(PerspectiveApiRequest request) {
        try {
            String url = config.getUrl() + "?key=" + config.getApiKey();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String requestJson = objectMapper.writeValueAsString(request);
            HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Perspective API 요청 실패: {}", e.getMessage(), e);
            throw new PerspectiveApiException(ErrorCode.PERSPECTIVE_API_REQUEST_ERROR);
        }
    }

    private double parseResponse(String responseJson) {
        return PerspectiveApiResponse.extractToxicityScore(responseJson);
    }
}
