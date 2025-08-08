package com.swyp.catsgotogedog.content.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.content.domain.entity.ContentWish;
import com.swyp.catsgotogedog.content.domain.entity.Hashtag;
import com.swyp.catsgotogedog.content.domain.entity.RegionCode;
import com.swyp.catsgotogedog.content.domain.response.ContentRankResponse;
import com.swyp.catsgotogedog.content.domain.response.RegionCodeResponse;
import com.swyp.catsgotogedog.content.repository.ContentRepository;
import com.swyp.catsgotogedog.content.repository.ContentWishRepository;
import com.swyp.catsgotogedog.content.repository.HashtagRepository;
import com.swyp.catsgotogedog.content.repository.RegionCodeRepository;
import com.swyp.catsgotogedog.content.repository.ViewLogRepository;
import com.swyp.catsgotogedog.review.service.ReviewService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContentRankService {

	private final ViewLogRepository viewLogRepository;
	private final ContentRepository contentRepository;
	private final HashtagRepository hashtagRepository;
	private final ContentWishRepository contentWishRepository;
	private final ContentSearchService contentSearchService;
	private final RegionCodeRepository regionCodeRepository;

	@Transactional(readOnly = true)
	public List<ContentRankResponse> fetchContentRank(String strUserId) {
		int userId = strUserId.equals("anonymousUser") ? 0 : Integer.parseInt(strUserId);
		LocalDateTime startDate = LocalDateTime.now().minusDays(1);

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

		List<ContentWish> userWishes = contentWishRepository.findByUserIdAndContentContentIdIn(
			userId, topContentIds
		);

		Set<Integer> wishedContentIds = userWishes.stream()
			.map(wish -> wish.getContent().getContentId())
			.collect(Collectors.toSet());

		AtomicInteger rankCounter = new AtomicInteger(1);

		return topContentIds.stream()
			.map(contentId -> {
				Content content = contentMap.get(contentId);
				RegionCode sidoName = regionCodeRepository.findBySidoCode(content.getSidoCode());
				RegionCode sigunguName = regionCodeRepository.findByParentCodeAndSigunguCode(content.getSidoCode(), content.getSigunguCode())
					.orElse(RegionCode.builder().regionName("").build());
				List<String> contentHashtags = hashtagsByContentId.getOrDefault(contentId, Collections.emptyList());
				boolean isWished = wishedContentIds.contains(contentId);
				int currentRank = rankCounter.getAndIncrement();

				return ContentRankResponse.builder()
					.contentId(content.getContentId())
					.title(content.getTitle())
					.image(content.getImage())
					.thumbImage(content.getThumbImage())
					.contentTypeId(content.getContentTypeId())
					.mapx(content.getMapx())
					.mapy(content.getMapy())
					.hashtag(contentHashtags)
					.avgScore(contentSearchService.getAverageScore(contentId))
					.wishData(isWished)
					.ranking(currentRank)
					.restDate(contentSearchService.getRestDate(contentId))
					.categoryId(content.getCategoryId())
					.regionName(new RegionCodeResponse(sidoName.getRegionName(), sigunguName.getRegionName()))
					.build();
			})
			.toList();
	}
}
