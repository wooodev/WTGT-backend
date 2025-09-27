package com.swyp.catsgotogedog.mypage.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.swyp.catsgotogedog.global.CatsgotogedogApiResponse;
import com.swyp.catsgotogedog.mypage.domain.response.ContentWishPageResponse;
import com.swyp.catsgotogedog.mypage.domain.response.LastViewHistoryResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "MyPage", description = "마이페이지 관련 API")
public interface MyPageHistoryControllerSwagger {

	@Operation(
		summary = "최근 본 장소 목록 조회",
		description = "사용자 인증을 통해 최근 본 장소 리스트를 최근 20개 조회합니다."
	)
	@SecurityRequirement(name = "bearer-key")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "장소 조회 성공"
			, content = @Content(schema = @Schema(implementation = LastViewHistoryResponse.class))),
		@ApiResponse(responseCode = "400", description = "요청 값이 누락되거나 유효하지 않음"
			, content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
		@ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"
			, content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class)))
	})
	ResponseEntity<CatsgotogedogApiResponse<?>> lastViewedHistory(
		@Parameter(hidden = true)
		@AuthenticationPrincipal String userId
	);

	@Operation(
		summary = "찜 목록 조회",
		description = "사용자 인증을 통해 사용자가 찜한 목록을 조회합니다."
	)
	@SecurityRequirement(name = "bearer-key")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "찜 목록 조회 성공"
			, content = @Content(schema = @Schema(implementation = ContentWishPageResponse.class))),
		@ApiResponse(responseCode = "400", description = "요청 값이 누락되거나 유효하지 않음"
			, content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
		@ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"
			, content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class)))
	})
	ResponseEntity<CatsgotogedogApiResponse<?>> fetchWishedContent(
		@Parameter(hidden = true)
		@AuthenticationPrincipal String userId,
		@Parameter(description = "요청 페이지") int page,
		@Parameter(description = "페이지당 결과 갯수") int size
	);
}
