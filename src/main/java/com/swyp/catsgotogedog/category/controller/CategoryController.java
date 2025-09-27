package com.swyp.catsgotogedog.category.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swyp.catsgotogedog.category.service.CategoryService;
import com.swyp.catsgotogedog.global.CatsgotogedogApiResponse;
import com.swyp.catsgotogedog.review.service.ReviewReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/code")
public class CategoryController implements CategoryControllerSwagger {

	private final CategoryService categoryService;
	private final ReviewReportService reviewReportService;

	@Override
	@GetMapping("/regionCode")
	public ResponseEntity<CatsgotogedogApiResponse<?>> fetchRegionCodes(
		@RequestParam(name = "시/도 코드", required = false)
		Integer sidoCode,
		@RequestParam(name = "시군구 코드", required = false)
		Integer sigunguCode) {
		return ResponseEntity.ok(CatsgotogedogApiResponse.success(
			"지역 코드 조회 성공",
			categoryService.findRegions(sidoCode, sigunguCode)
			));
	}

	@Override
	@GetMapping("/reasonCode")
	public ResponseEntity<CatsgotogedogApiResponse<?>> fetchResons() {

		return ResponseEntity.ok(CatsgotogedogApiResponse.success(
			"신고 사유 목록 조회 성공",
			reviewReportService.fetchReasons()));
	}
}
