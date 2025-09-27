package com.swyp.catsgotogedog.mypage.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.swyp.catsgotogedog.global.CatsgotogedogApiResponse;
import com.swyp.catsgotogedog.mypage.service.MyPageHistoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mypage")
@Slf4j
public class MyPageHistoryController implements MyPageHistoryControllerSwagger {

	private final MyPageHistoryService myPageHistoryService;

	@Override
	@GetMapping("/history")
	public ResponseEntity<CatsgotogedogApiResponse<?>> lastViewedHistory(
		@AuthenticationPrincipal String userId) {

		return ResponseEntity.ok(CatsgotogedogApiResponse.success("최근 방문 컨텐츠 조회 성공",
			myPageHistoryService.fetchLastViewHistory(userId)));
	}

	@Override
	@GetMapping("/wish")
	public ResponseEntity<CatsgotogedogApiResponse<?>> fetchWishedContent(
		@AuthenticationPrincipal String userId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "8")  int size) {

		Pageable pageable = PageRequest.of(page, size);

		return ResponseEntity.ok(CatsgotogedogApiResponse.success(
			"찜 목록 조회 성공", myPageHistoryService.fetchWishLists(userId, pageable)));
	}
}
