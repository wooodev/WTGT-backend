package com.swyp.catsgotogedog.global;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/batch")
public class BatchGeneratorController {

	private final HashtagGenerator hashtagGenerator;

	@Operation(summary = "해시태그 배치 수동 조작",
		description = "호출시 서버 배치가 동작하므로 주의 필요한 경우 제외 호출 금지")
	@GetMapping("/hashtag")
	public ResponseEntity<String> generateHashtags() {
		hashtagGenerator.generateHashtags();
		return ResponseEntity.ok("시작");
	}
}
