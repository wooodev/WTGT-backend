package com.swyp.catsgotogedog.aiplanner.controller;

import org.springframework.http.ResponseEntity;

import com.swyp.catsgotogedog.aiplanner.request.PlannerRequest;
import com.swyp.catsgotogedog.aiplanner.response.AiplannerResponse;
import com.swyp.catsgotogedog.global.CatsgotogedogApiResponse;
import com.swyp.catsgotogedog.pet.domain.response.PetProfileResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Ai플래너", description = "AI 플래너 관련 API")
public interface AiplannerControllerSwagger {
	
	@Operation(
		summary = "AI 플래너 생성",
		description = "AI를 통한 플랜 생성"
	)
	@SecurityRequirement(name = "bearer-key")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "플랜 생성 성공",
			content = @Content(schema = @Schema(implementation = AiplannerResponse.class))),
		@ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
			content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
		@ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
			content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class)))
	})
	ResponseEntity<CatsgotogedogApiResponse<?>> createAiPlanner(
		@Parameter(hidden = true)
		String userId,
		@RequestBody(description = """
			duration은 DAY_TRIP, ONE_NIGHT, TWO_NIGHT 중 하나의 데이터를 입력해야합니다.
			mood는 사용자가 입력한 기분 데이터입니다.
			""", required = true)
		PlannerRequest request
	);
}
