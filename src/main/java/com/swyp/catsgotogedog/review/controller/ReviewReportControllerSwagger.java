package com.swyp.catsgotogedog.review.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.swyp.catsgotogedog.global.CatsgotogedogApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Review", description = "리뷰 관련 API")
public interface ReviewReportControllerSwagger {

	@Operation(
		summary = "특정 리뷰를 신고합니다.",
		description = "사용자 인증을 통해 리뷰를 신고합니다."
	)
	@SecurityRequirement(name = "bearer-key")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "리뷰 삭제 성공"
			, content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
		@ApiResponse(responseCode = "400", description = "요청 값이 누락되거나, 이미 신고 처리된 리뷰"
			, content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
		@ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"
			, content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
		@ApiResponse(responseCode = "404", description = "리뷰 또는 유저가 존재하지 않음"
			, content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class)))
	})
	ResponseEntity<CatsgotogedogApiResponse<?>> reportReview(
		@Parameter(hidden = true)
		@AuthenticationPrincipal String userId,

		@Parameter(description = "신고할 리뷰 ID", required = true)
		@PathVariable int reviewId,

		@Parameter(description = "신고 사유 ID", required = true)
		@RequestBody int reasonId
	);
}
