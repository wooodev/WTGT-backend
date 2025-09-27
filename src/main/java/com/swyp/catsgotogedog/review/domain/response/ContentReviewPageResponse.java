package com.swyp.catsgotogedog.review.domain.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record ContentReviewPageResponse (
	@Schema(description = "총 조회 갯수")
	int totalElements,
	@Schema(description = "총 페이지 갯수")
	int totalPages,
	@Schema(description = "현재 페이지")
	int currentPage,
	@Schema(description = "사이즈")
	int size,
	@Schema(description = "다음 존재 여부")
	boolean hasNext,
	@Schema(description = "이전 존재 여부")
	boolean hasPrevious,
	@Schema(description = "컨텐츠 리뷰 목록")
	List<ReviewResponse> reviews,
	@Schema(description = "리뷰 전체 이미지 목록")
	List<ReviewImageResponse> reviewImages
) {
}
