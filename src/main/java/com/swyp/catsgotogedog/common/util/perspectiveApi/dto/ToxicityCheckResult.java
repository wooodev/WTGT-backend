package com.swyp.catsgotogedog.common.util.perspectiveApi.dto;

/**
 * 독성 검사 결과를 나타내는 DTO 클래스
 *
 * @param passed 검사 통과 여부
 * @param toxicityScore 독성 점수 (0.0 ~ 1.0)
 * @param threshold 독성 점수 임계값
 * @param text 검사 대상 텍스트
 * @param errorMessage 오류 메시지 (검사 실패 시)
 */
public record ToxicityCheckResult(boolean passed, double toxicityScore, double threshold, String text,
                                  String errorMessage) {

    public static ToxicityCheckResult success(String text, double toxicityScore, double threshold) {
        boolean passed = toxicityScore <= threshold;
        return new ToxicityCheckResult(passed, toxicityScore, threshold, text, null);
    }

    public static ToxicityCheckResult failure(String text, double threshold, String errorMessage) {
        return new ToxicityCheckResult(false, -1.0, threshold, text, errorMessage);
    }
}
