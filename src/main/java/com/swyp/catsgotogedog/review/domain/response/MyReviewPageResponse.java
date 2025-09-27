package com.swyp.catsgotogedog.review.domain.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record MyReviewPageResponse(
	@Schema(description = "총 조회 갯수")
	int totalElements,
	@Schema(description = "총 페이지")
	int totalPages,
	@Schema(description = "현재 조회 페이지")
	int currentPage,
	@Schema(description = "사이즈")
	int size,
	@Schema(description = "다음 존재 여부")
	boolean hasNext,
	@Schema(description = "이전 존재 여부")
	boolean hasPrevious,
	@Schema(description = "작성 리뷰 목록")
	List<MyReviewResponse> reviews
) {}
