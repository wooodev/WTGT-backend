package com.swyp.catsgotogedog.review.domain.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record MyReviewResponse(
	@Schema(description = "컨텐츠 ID")
	int contentId,
	@Schema(description = "컨텐츠 제목")
	String contentTitle,
	@Schema(description = "리뷰 ID")
	int reviewId,
	@Schema(description = "리뷰 내용")
	String content,
	@Schema(description = "별점")
	BigDecimal score,
	@Schema(description = "좋아요 수")
	int recommendedNumber,
	@Schema(description = "작성일시")
	LocalDateTime createdAt,
	@Schema(description = "이미지 목록")
	List<ReviewImageResponse> images
) {}
