package com.swyp.catsgotogedog.content.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.swyp.catsgotogedog.content.service.ContentRankService;
import com.swyp.catsgotogedog.global.CatsgotogedogApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/content")
public class ContentRankController implements ContentRankControllerSwagger {

	private final ContentRankService contentRankService;

	@Override
	@GetMapping("/rank")
	public ResponseEntity<CatsgotogedogApiResponse<?>> fetchContentRank(
		@AuthenticationPrincipal String userId
	) {
		return ResponseEntity.ok(
			CatsgotogedogApiResponse.success("조회 성공", contentRankService.fetchContentRank(userId))
		);
	}
}
