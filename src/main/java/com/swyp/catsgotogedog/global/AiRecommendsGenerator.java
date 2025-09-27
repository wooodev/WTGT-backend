package com.swyp.catsgotogedog.global;

import com.swyp.catsgotogedog.content.repository.AiRecommendsRepository;
import com.swyp.catsgotogedog.content.service.AiRecommendsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
@Slf4j
public class AiRecommendsGenerator {

    private final AiRecommendsRepository aiRecommendsRepository;
    private final AiRecommendsService aiRecommendsService;

    private static final String AI_RECOMMENDS_SCHEDULER_LOCK_KEY = "ai_recommends_scheduler";

    @Async
    @Scheduled(cron = "0 30 0 * * *")
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void generateAiRecommends() {
        log.info("AI 추천 생성 배치 시작");
        aiRecommendsRepository.deleteAllInBatch();
        log.info("기존 AI 추천 데이터 삭제 완료");
        for (int i = 0; i < 4; i++) {
            log.info("AI 추천 생성 중: {} 번째 반복 (횟수당 5개 생성)", i + 1);
            aiRecommendsService.getRandomContentsWithImages()
                    .forEach(aiRecommendsService::createAndSaveAiRecommend);
        }
        log.info("AI 추천 생성 배치 완료");
    }
}
