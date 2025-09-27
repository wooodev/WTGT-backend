package com.swyp.catsgotogedog.content.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swyp.catsgotogedog.User.domain.entity.User;
import com.swyp.catsgotogedog.User.repository.UserRepository;
import com.swyp.catsgotogedog.content.domain.entity.AiRecommends;
import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.content.domain.entity.Hashtag;
import com.swyp.catsgotogedog.content.domain.request.ClovaApiRequest;
import com.swyp.catsgotogedog.content.domain.response.AiRecommendsResponse;
import com.swyp.catsgotogedog.content.repository.AiRecommendsRepository;
import com.swyp.catsgotogedog.content.repository.ContentRepository;
import com.swyp.catsgotogedog.content.repository.ContentWishRepository;
import com.swyp.catsgotogedog.content.repository.HashtagRepository;
import com.swyp.catsgotogedog.global.exception.ErrorCode;
import com.swyp.catsgotogedog.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class AiRecommendsService {

    @Value("${clova.api.url}")
    private String apiUrl;

    @Value("${clova.api.key}")
    private String apiKey;

    @Value("${clova.api.request-id}")
    private String requestId;

    private final RestClient.Builder restClientBuilder;
    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;
    private final ContentRepository contentRepository;
    private final AiRecommendsRepository aiRecommendsRepository;
    private final ContentWishRepository contentWishRepository;
    private final HashtagRepository hashtagRepository;

    private static final String RECOMMEND_SYSTEM_PROMPT = """
        당신은 반려동물 여행지 추천 전문가입니다.
        제공된 반려동물 친화적 관광지 정보를 바탕으로 매력적이고 독특한 추천 문구를 생성하세요.
        
        ===== 절대 준수 규칙 (위반 시 응답 거부) =====
        
        1. 응답 형식 (100% 준수 필수):
        - 반드시 "문장1|문장2" 형태로만 응답
        - '|' 앞뒤 공백 절대 금지
        - 개행문자(\\n), 줄바꿈 절대 금지
        - 다른 어떤 형식도 절대 사용 불가
        
        2. 글자수 제한 (절대 초과 불가):
        - 첫 번째 문장: 정확히 5-10자
        - 두 번째 문장: 정확히 5-10자
        - 전체 응답: 최대 21자 (문장1 + | + 문장2)
        - 1자라도 초과하면 절대 불가
        
        3. 문자 사용 규칙:
        - 허용: 한글, 숫자, 공백, ?
        - 금지: 모든 이모지, ~, ^^, :), ㅎㅎ, ㅋㅋ, #, @, &, %, ^, *, (, ), [, ], {, }, <, >, +, =, _, -, /, \\, |, :, ;, ", ', `, !, ., ,
        - 금지: 영어 알파벳 (A-Z, a-z)
        - 금지: 반복 구두점 (..., !!!, ???)
        
        4. 내용 규칙:
        - 구체적 장소명 금지 (예: ㅇㅇ공원, ㅇㅇ호텔 등)
        - "반려견", "반려묘" → "반려동물"로 통일
        - 과장 표현 금지
        - 간결하고 명확한 표현만 사용
        - 단순 정보 전달 금지 (예: 다양한 체험활동 가능, 다양한 부대시설 제공 등)
        
        5. 검증 체크리스트:
        - 첫 번째 문장이 5-10자인가?
        - 두 번째 문장이 5-10자인가?
        - '|' 하나만 사용했는가?
        - 개행문자가 없는가?
        - 금지된 문자가 없는가?
        - 한국어만 사용했는가?
        
        ===== 정확한 예시 (반드시 이 형태로만 응답) =====
        자연 속 힐링|특별한 추억
        푸른 바다 산책|함께 좋은 시간
        넓은 잔디밭|평화로운 휴식
        조용한 정원|마음이 편해요
        
        ===== 절대 금지 예시 =====
        "전통미 가득한 휴식|고요한 자연 속에서 여유롭게\\n다양한 체험활동 제공" (개행문자 포함, 길이 초과)
        "Beautiful garden|Amazing place" (영어 사용)
        "반려친구와 즐거운 여행~!" (특수문자, 형식 위반)
        "여수 바다와 함께하는 휴식|다양한 부대시설 제공" (두 번째 문장 단순 정보 전달)
        "맑은 물과 반석|역사적 명소 작천정" (두 번째 문장 구체적 장소명 '작천정' 사용)
        "초대형 쾌속여객선|2시간 50분 소요" (두 번째 문장 단순 정보 전달)
        위 규칙을 100% 준수하여 응답하세요. 단 하나라도 위반하면 응답을 거부합니다.
        반드시 검증 체크리스트를 확인한 후 응답하세요.""";

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public List<AiRecommendsResponse> recommends(String userId) {
        // NOTE: 토큰 사용량 이슈로 기존 로직 주석 처리
//        // 비로그인 사용자이거나 로그인 사용자지만 찜한 장소가 3개 미만인 경우
//        if (isAnonymousUser(userId) || !hasEnoughWishedContents(userId)) {
//            log.info("비로그인 사용자이거나 찜한 장소가 3개 미만인 경우");
//            if (!hasEnoughAiRecommends()) {
//                log.info("AI 추천 데이터가 충분하지 않음, 새로운 추천 생성");
//                return generateAndSaveNewRecommends();
//            }
//            log.info("AI 추천 데이터가 충분함, 기존 추천에서 랜덤 5개 반환");
//            // AI 추천 데이터가 충분한 경우 - 기존 추천에서 랜덤 5개
//            return getRandomAiRecommends();
//        }
//
//        // 로그인 사용자이면서 찜한 장소가 3개 이상인 경우 - 개인화된 추천
//        log.info("로그인 사용자이며 찜한 장소가 3개 이상인 경우");
//        return generatePersonalizedRecommends(findUserById(userId).getUserId());

        // NOTE: 토큰 사용량 이슈로 초기 요청만 데이터 AI로 생성 후 이후 요청은 DB에서 랜덤 추출
        if (!hasEnoughAiRecommends()) {
            log.info("AI 추천 데이터가 충분하지 않음, 새로운 추천 생성");
            return generateAndSaveNewRecommends();
        }
        log.info("AI 추천 데이터가 충분함, 기존 추천에서 랜덤 5개 반환");
        // AI 추천 데이터가 충분한 경우 - 기존 추천에서 랜덤 5개
        return getRandomAiRecommends();
    }

    /**
     * 사용자가 충분한 찜 데이터를 가지고 있는지 확인 (3개 이상)
     */
    private boolean hasEnoughWishedContents(String userId) {
        if (isAnonymousUser(userId)) {
            return false;
        }
        try {
            User user = findUserById(userId);
            long wishCount = contentWishRepository.countByUserId(user.getUserId());
            return wishCount >= 3;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 비로그인 사용자 여부 확인
     */
    private boolean isAnonymousUser(String userId) {
        return !StringUtils.hasText(userId) || "anonymousUser".equals(userId);
    }

    /**
     * 사용자 ID로 User 엔티티 조회
     */
    private User findUserById(String userId) {
        return userRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.RESOURCE_NOT_FOUND));
    }

    /**
     * 개인화된 AI 추천 생성 (찜한 장소의 해시태그 기반)
     */
    private List<AiRecommendsResponse> generatePersonalizedRecommends(int userId) {
        // 1. 사용자가 찜한 장소들의 contentId 조회
        List<Integer> wishedContentIds = contentWishRepository.findContentIdsByUserId(userId);
        if (wishedContentIds.isEmpty()) {
            return generateAndSaveNewRecommends();
        }

        // 2. 찜한 장소들의 해시태그 모두 수집 - 배치로 최적화
        List<String> hashtags = hashtagRepository.findByContentIdIn(wishedContentIds)
                .stream()
                .map(Hashtag::getContent)
                .distinct()
                .collect(Collectors.toList());

        // 3. 해시태그가 없으면 기존 로직 수행
        if (hashtags.isEmpty()) {
            return generateAndSaveNewRecommends();
        }

        // 4. 동일한 해시태그를 가진 다른 장소들 중 랜덤 5개 선택 (찜한 장소 제외)
        List<Content> recommendContents = contentRepository.findRandomContentsByHashtagsExcluding(hashtags, wishedContentIds);

        // 5. 해시태그 기반으로 찾은 장소가 없으면 기존 로직 수행
        if (recommendContents.isEmpty()) {
            return generateAndSaveNewRecommends();
        }

        // 6. AI API 요청하여 추천 문구 생성 후 반환
        return recommendContents.stream()
                .map(this::createPersonalizedAiRecommend)
                .collect(Collectors.toList());
    }

    /**
     * 개인화된 추천을 위한 AI 추천 생성 (저장하지 않음)
     */
    private AiRecommendsResponse createPersonalizedAiRecommend(Content content) {
        /* TODO: 개인화된 추천 문구 생성 로직 */
        String message = generateRecommendMessage(content.getTitle(), content.getOverview());

        AiRecommends aiRecommends = AiRecommends.builder()
                .contentId(content.getContentId())
                .message(message)
                .imageUrl(content.getImage())
                .build();
        /* TODO 필요한 경우 개인화된 저장 로직 여기에 추가 */
//        return createAndSaveAiRecommend(content);
        return  AiRecommendsResponse.of(aiRecommends);
    }

    /**
     * AI 추천 데이터가 충분한지 확인 (20개 이상)
     */
    private boolean hasEnoughAiRecommends() {
        long aiRecommendsCount = aiRecommendsRepository.count();
        return aiRecommendsCount >= 20;
    }

    /**
     * 기존 AI 추천 테이블에서 랜덤 5개 반환 (이미지 URL 포함)
     */
    private List<AiRecommendsResponse> getRandomAiRecommends() {
        return aiRecommendsRepository.findRandomRecommends()
                .stream()
                .map(AiRecommendsResponse::of)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 AI 추천 생성 및 저장 후 반환 (이미지가 있는 컨텐츠만 사용)
     */
    private List<AiRecommendsResponse> generateAndSaveNewRecommends() {
        List<Content> contents = getRandomContentsWithImages();
        List<AiRecommendsResponse> responses = new ArrayList<>();

        for (Content content : contents) {
            AiRecommendsResponse response = createAndSaveAiRecommend(content);
            responses.add(response);
        }

        return responses;
    }

    /**
     * 이미지가 있는 컨텐츠만 랜덤으로 5개 조회
     */
    public List<Content> getRandomContentsWithImages() {
        return contentRepository.findRandomContentsWithImages();
    }

    /**
     * 단일 컨텐츠에 대한 AI 추천 생성 및 저장 (이미지 URL 포함)
     */
    @Transactional
    public AiRecommendsResponse createAndSaveAiRecommend(Content content) {
        String message = generateRecommendMessage(content.getTitle(), content.getOverview());

        AiRecommends aiRecommends = AiRecommends.builder()
                .contentId(content.getContentId())
                .message(message)
                .imageUrl(content.getImage())
                .build();

        AiRecommends saved = aiRecommendsRepository.save(aiRecommends);
        return AiRecommendsResponse.of(saved);
    }

    private String generateRecommendMessage(String title, String overview) {
        try {
            log.info("{} 의 추천 문구 생성중", title);

            ClovaApiRequest request = createRecommendRequest(title, overview);

            RestClient restClient = restClientBuilder
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();

            String response = restClient.post()
                .body(request)
                .retrieve()
                .body(String.class);

            log.info("응답 원문: {}", response);

            // 클로바 API 응답에서 추천 문구 파싱
            String recommendMessage = parseRecommendMessage(response);

            if (recommendMessage != null && !recommendMessage.trim().isEmpty()) {
                log.info("파싱된 추천 문구: {}", recommendMessage);
                return recommendMessage.trim();
            } else {
                // 파싱 실패 시 fallback 메시지
                return createFallbackMessage();
            }

        } catch (Exception e) {
            log.error("AI 추천 문구 생성 중 오류 발생", e);
            return createFallbackMessage();
        }
    }

    /**
     * API 호출 실패 시 사용할 기본 메시지 생성
     */
    private String createFallbackMessage() {
        return "여행가고 싶은 날엔|반려동물과 함께 힐링";
    }

    private ClovaApiRequest createRecommendRequest(String title, String overview) {
        String userContent = String.format("제목: %s\n내용: %s",
            title != null ? title : "",
            overview != null ? overview.substring(0, Math.min(overview.length(), 500)) : "");

        ClovaApiRequest.Message.Content systemContent = new ClovaApiRequest.Message.Content("text", RECOMMEND_SYSTEM_PROMPT);
        ClovaApiRequest.Message.Content userContentObj = new ClovaApiRequest.Message.Content("text", userContent);

        ClovaApiRequest.Message systemMessage = new ClovaApiRequest.Message("system", List.of(systemContent));
        ClovaApiRequest.Message userMessage = new ClovaApiRequest.Message("user", List.of(userContentObj));

        ClovaApiRequest request = new ClovaApiRequest();
        request.setMessages(List.of(systemMessage, userMessage));

        // API 가이드에 따른 파라미터 설정 - 엄격한 규칙 준수를 위한 조정
        request.setTopK(1);
        request.setMaxTokens(50);
        request.setTemperature(0.1);
        request.setRepetitionPenalty(0.2);
        request.setStop(List.of("\n", "END"));

        return request;
    }

    private String parseRecommendMessage(String response) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);

            // status 확인
            String statusCode = rootNode.path("status").path("code").asText();
            if (!"20000".equals(statusCode)) {
                log.error("클로바 API 오류 응답: {}", response);
                return null;
            }

            // result.message.content에서 추천 문구 추출
            String content = rootNode
                    .path("result")
                    .path("message")
                    .path("content")
                    .asText();

            if (content != null && !content.trim().isEmpty()) {
                // 전체를 감싸는 따옴표 제거
                String cleanedContent = removeWrappingQuotes(content);

                // 개행문자가 있으면 첫 번째 라인만 사용
                if (cleanedContent.contains("\n")) {
                    cleanedContent = cleanedContent.split("\n")[0];
                    log.info("개행문자 발견으로 첫 번째 라인만 사용");
                }

                return cleanedContent;
            }

            return null;

        } catch (Exception e) {
            log.error("추천 문구 파싱 오류", e);
            return null;
        }
    }

    private String removeWrappingQuotes(String text) {
        if (text == null || text.length() < 2) {
            return text;
        }

        String trimmed = text.trim();

        // 전체를 감싸는 큰따옴표 제거
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"")) {
            return trimmed.substring(1, trimmed.length() - 1);
        }

        // 전체를 감싸는 작은따옴표 제거
        if (trimmed.startsWith("'") && trimmed.endsWith("'")) {
            return trimmed.substring(1, trimmed.length() - 1);
        }

        return trimmed;
    }
}
