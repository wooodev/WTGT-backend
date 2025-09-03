package com.swyp.catsgotogedog.content.controller;

import com.swyp.catsgotogedog.content.domain.request.ContentRequest;
import com.swyp.catsgotogedog.content.domain.response.AiRecommendsResponse;
import com.swyp.catsgotogedog.content.domain.response.ContentResponse;
import com.swyp.catsgotogedog.content.domain.response.LastViewHistoryResponse;
import com.swyp.catsgotogedog.content.domain.response.PlaceDetailResponse;
import com.swyp.catsgotogedog.content.service.AiRecommendsService;
import com.swyp.catsgotogedog.content.service.ContentSearchService;
import com.swyp.catsgotogedog.content.service.ContentService;
import com.swyp.catsgotogedog.global.CatsgotogedogApiResponse;
import org.apache.commons.lang3.math.NumberUtils;
import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/content")
public class ContentController implements ContentControllerSwagger{
    private final ContentService contentService;
    private final ContentSearchService contentSearchService;
    private final AiRecommendsService aiRecommandService;

    @GetMapping("/search")
    public ResponseEntity<List<ContentResponse>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String sido,
            @RequestParam(required = false) List<String> sigungu,
            @RequestParam(required = false) Integer contentTypeId,
            @AuthenticationPrincipal String principal) {

        String userId = null;
        if (StringUtils.hasText(principal) && NumberUtils.isCreatable(principal)) {
            userId = principal;
        }

        List<ContentResponse> list = contentSearchService.search(title, sido, sigungu, contentTypeId, userId);

        return list.isEmpty()
                ? ResponseEntity.noContent().build()   // 204
                : ResponseEntity.ok(list);             // 200
    }

    @PostMapping("/save")
    ResponseEntity<Void> saveContent(@RequestBody ContentRequest request) {
        contentService.saveContent(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/savelist")
    public ResponseEntity<?> saveList(@RequestBody List<ContentRequest> requests) {
        requests.forEach(contentService::saveContent);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/placedetail")
    public ResponseEntity<PlaceDetailResponse> getPlaceDetail(
            @RequestParam int contentId,
            @AuthenticationPrincipal String principal,
            @RequestParam int contentTypeId){

        String userId = null;
        if (StringUtils.hasText(principal) && NumberUtils.isCreatable(principal)) {
            userId = principal;
        }

        PlaceDetailResponse placeDetailResponse = contentService.getPlaceDetail(contentId,userId,contentTypeId);
        return ResponseEntity.ok(placeDetailResponse);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<LastViewHistoryResponse>> getRecentViews(@AuthenticationPrincipal String userId) {
        List<LastViewHistoryResponse> recent = contentService.getRecentViews(userId);
        return ResponseEntity.ok().body(recent);
    }


    @PostMapping("/wish-check")
    public ResponseEntity<?> checkWish(
            @AuthenticationPrincipal String userId,
            @RequestParam int contentId
    ) {
        boolean checkWish = contentService.checkWish(userId, contentId);
        return ResponseEntity.ok(Map.of("checkWish", checkWish));
    }

    @PostMapping("/visited-check")
    public ResponseEntity<?> checkVisited(
            @AuthenticationPrincipal String userId,
            @RequestParam int contentId
    ) {
        boolean visited = contentService.checkVisited(userId, contentId);
        return ResponseEntity.ok(Map.of("visited", visited));

    }
    @GetMapping("/ai/recommends")
    public ResponseEntity<CatsgotogedogApiResponse<List<AiRecommendsResponse>>> recommendContents(
            @AuthenticationPrincipal String userId) {
        List<AiRecommendsResponse> recommendations = aiRecommandService.recommends(userId);
        return ResponseEntity.ok(
                CatsgotogedogApiResponse.success("AI 추천 장소 조회 성공", recommendations));
    }

    @Override
    @PostMapping("/recent/{contentId}")
    public ResponseEntity<CatsgotogedogApiResponse<?>> lastViewedHistory(
        @AuthenticationPrincipal String userId,
        @PathVariable int contentId
    ) {
        contentService.saveLastViewedContent(userId, contentId);
        return ResponseEntity.ok(
            CatsgotogedogApiResponse.success("최근 본 장소 저장 성공", null)
        );
    }
}
