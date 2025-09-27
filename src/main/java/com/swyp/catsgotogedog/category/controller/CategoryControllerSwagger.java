package com.swyp.catsgotogedog.category.controller;

import org.springframework.http.ResponseEntity;

import com.swyp.catsgotogedog.category.domain.response.RegionHierarchyResponse;
import com.swyp.catsgotogedog.category.domain.response.SubRegionResponse;
import com.swyp.catsgotogedog.global.CatsgotogedogApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Code", description = "코드 관련 API")
public interface CategoryControllerSwagger {

	@Operation(
		summary = "지역별 코드를 조회합니다.",
		description = "입력 파라미터별로 동적으로 반환합니다. " 
			+ "아무것도 입력하지 않을 경우 모든 sidoCode와 하위 sigunguCode까지 반환하며며," 
			+ "sidoCode만 입력할 경우 해당 sidoCode와 하위 sigunguCode까지 반환합니다." 
			+ "sigunguCode를 검색할 경우 sidoCode도 함께 필수로 입력해주어야 합니다."
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "지역 코드 조회 성공"
			, content = @Content(schema = @Schema(implementation = RegionHierarchyResponse.class))),
		@ApiResponse(responseCode = "400", description = "요청 값이 누락되거나 유효하지 않음"
			, content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
		@ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"
			, content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class)))
	})
	ResponseEntity<CatsgotogedogApiResponse<?>> fetchRegionCodes(
		@Parameter(description = "시/도 코드", required = false)
		Integer sidoCode,
		@Parameter(description = "시군구 코드", required = false)
		Integer sigunguCode
	);

	@Operation(
		summary = "신고 사유 목록을 조회합니다.",
		description = "리뷰 신고를 위한 신고 사유 목록입니다. 해당 사유 ID를 통해 리뷰 신고 처리를 진행해주세요"
	)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "사유 목록 조회 성공"
			, content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
		@ApiResponse(responseCode = "400", description = "요청 값이 누락되거나 유효하지 않음"
			, content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
		@ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"
			, content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
		@ApiResponse(responseCode = "403", description = "접근 권한이 없음, 다른 사람의 리뷰 삭제시"
			, content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
		@ApiResponse(responseCode = "404", description = "리뷰가 존재하지 않음"
			, content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class)))
	})
	ResponseEntity<CatsgotogedogApiResponse<?>> fetchResons();
}
