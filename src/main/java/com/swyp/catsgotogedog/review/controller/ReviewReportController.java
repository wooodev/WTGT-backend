package com.swyp.catsgotogedog.review.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swyp.catsgotogedog.global.CatsgotogedogApiResponse;
import com.swyp.catsgotogedog.review.service.ReviewReportService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review/report")
@Slf4j
public class ReviewReportController implements ReviewReportControllerSwagger {

	private final ReviewReportService reviewReportService;

	@Override
	@PostMapping(value = "/{reviewId}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public ResponseEntity<CatsgotogedogApiResponse<?>> reportReview(
		@AuthenticationPrincipal String userId,
		@PathVariable int reviewId,
		@RequestParam int reasonId) {

		reviewReportService.reportReview(reviewId, reasonId, userId);

		return ResponseEntity.ok(CatsgotogedogApiResponse.success(
			"리뷰 신고 완료", null));
	}
}
