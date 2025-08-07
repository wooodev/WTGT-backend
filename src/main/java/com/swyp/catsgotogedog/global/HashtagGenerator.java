package com.swyp.catsgotogedog.global;

import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.content.repository.ContentRepository;
import com.swyp.catsgotogedog.content.repository.HashtagRepository;
import com.swyp.catsgotogedog.content.service.HashtagServcie;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class HashtagGenerator {
	
	private final HashtagRepository hashtagRepository;
	private final ContentRepository contentRepository;
	private final HashtagServcie hashtagServcie;

	@Async
	@Scheduled(cron = "0 0 3 * * ?")
	public void generateHashtags() {
		List<Integer> contentIds = contentRepository.findAll().stream()
			.map(Content::getContentId)
			.filter(id -> !hashtagRepository.existsByContentId(id))
			.toList();
		
		for(Integer contentId : contentIds) {
			try {
				hashtagServcie.generateAndSaveHashTags(contentId);
				Thread.sleep(5000);
			} catch (Exception e) {
				log.error("해시태그 생성 배치 실행 중 오류 발생 ({})", contentId, e);
			}
		}
	}
}
