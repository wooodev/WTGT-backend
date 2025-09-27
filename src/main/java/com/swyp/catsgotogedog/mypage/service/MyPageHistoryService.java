package com.swyp.catsgotogedog.mypage.service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swyp.catsgotogedog.User.repository.UserRepository;
import com.swyp.catsgotogedog.content.domain.entity.ContentWish;
import com.swyp.catsgotogedog.content.repository.ContentWishRepository;
import com.swyp.catsgotogedog.global.exception.CatsgotogedogException;
import com.swyp.catsgotogedog.global.exception.ErrorCode;
import com.swyp.catsgotogedog.mypage.domain.entity.LastViewHistory;
import com.swyp.catsgotogedog.mypage.domain.response.ContentWishPageResponse;
import com.swyp.catsgotogedog.mypage.domain.response.ContentWishResponse;
import com.swyp.catsgotogedog.mypage.domain.response.LastViewHistoryResponse;
import com.swyp.catsgotogedog.mypage.repository.MyPageHistoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageHistoryService {

	private final MyPageHistoryRepository myPageHistoryRepository;
	private final UserRepository userRepository;
	private final ContentWishRepository contentWishRepository;

	public List<LastViewHistoryResponse> fetchLastViewHistory(String stringUserId) {
		Integer userId = Integer.parseInt(stringUserId);
		validateUser(userId);

		Pageable pageable = PageRequest.of(0, 20);

		List<LastViewHistory> histories = myPageHistoryRepository.findAllByUserId(userId, pageable);

		if(histories.isEmpty()) {
			return Collections.emptyList();
		}

		List<Integer> contentIdsFromHistory = histories.stream()
			.map(history -> history.getContent().getContentId())
			.toList();

		Set<Integer> wishedContentIds = contentWishRepository.findWishedContentIdsByUserIdAndContentIds(userId, contentIdsFromHistory);

		return histories.stream()
			.map(history -> {
				boolean isWished = wishedContentIds.contains(history.getContent().getContentId());
				return new LastViewHistoryResponse(
					history.getContent().getContentId(),
					history.getContent().getTitle(),
					history.getContent().getImage(),
					history.getContent().getThumbImage(),
					isWished
				);
			})
			.toList();
	}

	public ContentWishPageResponse fetchWishLists(String stringUserId, Pageable pageable) {
		Integer userId = Integer.parseInt(stringUserId);
		validateUser(userId);

		Pageable wishPageable =  PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());

		Page<ContentWish> wishPage = contentWishRepository.findAllByUserId(userId, wishPageable);

		return new ContentWishPageResponse(
			wishPage.stream()
				.map(wish -> new ContentWishResponse(
					wish.getContent().getContentId(),
					wish.getContent().getImage(),
					wish.getContent().getThumbImage(),
					Boolean.TRUE
				)).toList(),
			wishPage.getTotalElements(),
			wishPage.getTotalPages(),
			wishPage.getNumber(),
			wishPage.getSize(),
			wishPage.hasNext(),
			wishPage.hasPrevious()
		);
	}

	private void validateUser(Integer userId) {
		userRepository.findById(userId)
			.orElseThrow(() -> new CatsgotogedogException(ErrorCode.MEMBER_NOT_FOUND));
	}
}
