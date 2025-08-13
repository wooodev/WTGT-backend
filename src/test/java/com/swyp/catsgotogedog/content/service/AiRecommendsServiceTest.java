package com.swyp.catsgotogedog.content.service;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
class AiRecommendsServiceTest {

    @Autowired
    AiRecommendsService aiRecommendsService;

    @Autowired
    HashtagServcie hashtagServcie;   // 추가

    @Test
    void openAi_추천문구_생성() {
        String result = ReflectionTestUtils.invokeMethod(
                aiRecommendsService,
                "generateRecommendMessage",
                "해운대 해수욕장",
                "반려동물과 함께 산책할 수 있는 넓은 해변. 반려견 동반 가능 구역이 있습니다."
        );

        System.out.println("결과: " + result);
    }

    @Test
    void openAi_해시태그_생성() {   // 추가
        // private 메서드 직접 호출 → DB에 저장하지 않고 생성만 테스트
        List<String> result = ReflectionTestUtils.invokeMethod(
                hashtagServcie,
                "generateHashtags",
                "해운대 해수욕장",
                "반려동물과 함께 산책할 수 있는 넓은 해변. 반려견 동반 가능 구역이 있습니다."
        );

        System.out.println("결과: " + result);
    }
}