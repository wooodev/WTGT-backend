package com.swyp.catsgotogedog.content.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.content.domain.entity.Hashtag;
import com.swyp.catsgotogedog.content.domain.response.ContentRankResponse;
import com.swyp.catsgotogedog.content.repository.ContentRepository;
import com.swyp.catsgotogedog.content.repository.HashtagRepository;
import com.swyp.catsgotogedog.content.repository.ViewLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentRankService {

	private final ViewLogRepository viewLogRepository;
	private final ContentRepository contentRepository;
	private final HashtagRepository hashtagRepository;

	@Transactional(readOnly = true)
	public List<ContentRankResponse> fetchContentRank() {
		LocalDateTime startDate = LocalDateTime.now().minusWeeks(1);

		Pageable top20 = PageRequest.of(0, 20);

		List<Integer> topContentIds = viewLogRepository.findTopContentViews(startDate, top20);

		if(topContentIds == null || topContentIds.isEmpty()) {
			return Collections.emptyList();
		}

		Map<Integer, Content> contentMap = contentRepository.findAllById(topContentIds).stream()
				.collect(Collectors.toMap(Content::getContentId, Function.identity()));

		List<Hashtag> hashtags = hashtagRepository.findByContentIdIn(topContentIds);

		Map<Integer, List<String>> hashtagsByContentId = hashtags.stream()
				.collect(Collectors.groupingBy(
					Hashtag::getContentId,
					Collectors.mapping(Hashtag::getContent, Collectors.toList())
				));

		return topContentIds.stream()
			.map(contentId -> {
				Content content = contentMap.get(contentId);
				List<String> contentHashtags = hashtagsByContentId.getOrDefault(contentId, Collections.emptyList());

				return ContentRankResponse.builder()
					.contentId(content.getContentId())
					.title(content.getTitle())
					.image(content.getImage())
					.thumbImage(content.getThumbImage())
					.contentTypeId(content.getContentTypeId())
					.mapx(content.getMapx())
					.mapy(content.getMapy())
					.hashtags(contentHashtags)
					.build();
			})
			.toList();
	}
}
