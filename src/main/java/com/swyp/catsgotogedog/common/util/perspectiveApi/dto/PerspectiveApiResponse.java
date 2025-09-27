package com.swyp.catsgotogedog.common.util.perspectiveApi.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swyp.catsgotogedog.global.exception.ErrorCode;
import com.swyp.catsgotogedog.global.exception.PerspectiveApiException;
import lombok.extern.slf4j.Slf4j;

/**
 * Perspective API 응답 처리 유틸리티
 */
@Slf4j
public class PerspectiveApiResponse {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Perspective API 응답 JSON에서 독성 점수를 추출
     *
     * @param responseJson API 응답 JSON 문자열
     * @return 독성 점수 (0.0 ~ 1.0)
     * @throws PerspectiveApiException API 응답 파싱 실패 시
     */
    public static double extractToxicityScore(String responseJson) {
        try {
            JsonNode response = objectMapper.readTree(responseJson);

            // attributeScores.TOXICITY.summaryScore.value 경로로 점수 추출
            JsonNode attributeScores = response.get("attributeScores");
            if (attributeScores == null) {
                throw new PerspectiveApiException(ErrorCode.PERSPECTIVE_API_RESPONSE_ERROR);
            }

            JsonNode toxicity = attributeScores.get("TOXICITY");
            if (toxicity == null) {
                throw new PerspectiveApiException(ErrorCode.PERSPECTIVE_API_RESPONSE_ERROR);
            }

            JsonNode summaryScore = toxicity.get("summaryScore");
            if (summaryScore == null) {
                throw new PerspectiveApiException(ErrorCode.PERSPECTIVE_API_RESPONSE_ERROR);
            }

            JsonNode valueNode = summaryScore.get("value");
            if (valueNode == null) {
                throw new PerspectiveApiException(ErrorCode.PERSPECTIVE_API_RESPONSE_ERROR);
            }

            double score = valueNode.asDouble();
            log.debug("독성 점수 추출 완료: {}", score);

            return score;

        } catch (PerspectiveApiException e) {
            throw e;
        } catch (Exception e) {
            log.error("Perspective API 응답 파싱 실패: {}", e.getMessage(), e);
            throw new PerspectiveApiException(ErrorCode.PERSPECTIVE_API_RESPONSE_ERROR);
        }
    }
}
