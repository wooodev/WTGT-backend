package com.swyp.catsgotogedog.common.util.perspectiveApi.service;

import com.swyp.catsgotogedog.common.config.PerspectiveApiConfig;
import com.swyp.catsgotogedog.common.util.perspectiveApi.dto.ToxicityCheckResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ToxicityCheckService {

    private final PerspectiveApiService perspectiveApiService;
    private final PerspectiveApiConfig config;

    /**
     * 닉네임의 독성 점수를 체크합니다.
     * @param nickname 검사할 닉네임
     * @return ToxicityCheckResult 객체
     */
    public ToxicityCheckResult checkNickname(String nickname) {
        return checkText(nickname, config.getNicknameThreshold());
    }

    /**
     * 애완동물 이름의 독성 점수를 체크합니다.
     * @param petName 검사할 애완동물 이름
     * @return ToxicityCheckResult 객체
     */
    public ToxicityCheckResult checkPetName(String petName) {
        return checkText(petName, config.getPetNameThreshold());
    }

    /**
     * 독성 점수를 체크합니다.
     * @param text 검사할 텍스트
     * @param threshold 독성 점수 기준
     * @return ToxicityCheckResult 객체
     */
    public ToxicityCheckResult checkText(String text, double threshold) {
        if (text == null || text.trim().isEmpty()) {
            return ToxicityCheckResult.failure(text, threshold, "텍스트가 비어있습니다.");
        }
        try {
            double score = perspectiveApiService.getToxicityScore(text.trim());

            boolean passed = score <= threshold;
            log.debug("독성 검사 - 텍스트: '{}', 점수: {}, 기준: {}, 통과: {}",
                    text, score, threshold, passed);

            return ToxicityCheckResult.success(text, score, threshold);

        } catch (Exception e) {
            log.error("독성 검사 실패: {}", e.getMessage(), e);
            return ToxicityCheckResult.failure(text, threshold,
                    "독성 검사 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
