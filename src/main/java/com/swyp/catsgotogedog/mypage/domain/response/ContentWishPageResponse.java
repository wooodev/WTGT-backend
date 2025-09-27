package com.swyp.catsgotogedog.mypage.domain.response;

import java.util.List;

import com.swyp.catsgotogedog.content.domain.entity.ContentWish;
import com.swyp.catsgotogedog.review.domain.response.ReviewResponse;

public record ContentWishPageResponse(
	List<ContentWishResponse> wishes,
	Long totalElements,
	int totalPages,
	int currentPage,
	int size,
	boolean hasNext,
	boolean hasPrevious
) {
}
