package com.swyp.catsgotogedog.content.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestParam;

import com.swyp.catsgotogedog.content.domain.response.ContentRankResponse;
import com.swyp.catsgotogedog.content.domain.response.PlaceDetailResponse;
import com.swyp.catsgotogedog.global.CatsgotogedogApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Content", description = "컨텐츠 (관광지, 숙소, 음식점, 축제/공연/행사) 관련 API")
public interface ContentRankControllerSwagger {

	@Operation(
		summary = "인기 장소 조회",
		description = """
				24시간 동안 조회수가 가장 많은 장소를 반환합니다.
			"""
	)
	@SecurityRequirement(name = "bearer-key")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "조회 성공",
			content = @Content(schema = @Schema(implementation = ContentRankResponse.class))),
		@ApiResponse(responseCode = "500", description = "서버 내부 오류")
	})
	ResponseEntity<CatsgotogedogApiResponse<?>> fetchContentRank(
		@AuthenticationPrincipal String userId
	);
}
