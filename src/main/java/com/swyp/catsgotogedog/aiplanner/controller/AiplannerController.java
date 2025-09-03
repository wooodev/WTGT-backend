package com.swyp.catsgotogedog.aiplanner.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swyp.catsgotogedog.aiplanner.request.PlannerRequest;
import com.swyp.catsgotogedog.aiplanner.service.AiPlannerService;
import com.swyp.catsgotogedog.global.CatsgotogedogApiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/planner")
@Slf4j
public class AiplannerController implements AiplannerControllerSwagger {

	private final AiPlannerService aiPlannerService;

	@PostMapping("/initEmbeddingData")
	public void initEmbeddingData() {
		aiPlannerService.initEmbedContentData();
	}

	@PostMapping("/recommend")
	public ResponseEntity<CatsgotogedogApiResponse<?>> createAiPlanner(
		@AuthenticationPrincipal String userId,
		@RequestBody PlannerRequest request
	) {
		return ResponseEntity.ok(
			CatsgotogedogApiResponse.success("플래너 생성 성공", aiPlannerService.createPlan(userId, request))
		);
	}
}
