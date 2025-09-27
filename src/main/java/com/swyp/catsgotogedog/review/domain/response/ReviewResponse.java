package com.swyp.catsgotogedog.review.domain.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

public record ReviewResponse (
	@Schema(description = "조회 컨텐츠 ID")
	int contentId,
	@Schema(description = "리뷰 ID")
	int reviewId,
	@Schema(description = "리뷰 작성자 ID")
	int userId,
	@Schema(description = "리뷰 작성자 닉네임")
	String displayName,
	@Schema(description = "리뷰 작성자 프로필 이미지")
	String profileImageUrl,
	@Schema(description = "리뷰 내용")
	String content,
	@Schema(description = "별점")
	BigDecimal score,
	@Schema(description = "리뷰 작성일시")
	LocalDateTime createdAt,
	@Schema(description = "리뷰 추천수")
	int recommendedNumber,
	@Schema(description = "자신의 추천 여부")
	boolean isRecommended,
	@Schema(description = "신고 누적 5회 이상 리뷰 여부")
	boolean isBlind,
	@Schema(description = "리뷰 이미지 목록")
	List<ReviewImageResponse> images
) {}
