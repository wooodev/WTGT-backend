package com.swyp.catsgotogedog.mypage.domain.response;

public record ContentWishResponse (
	int contentId,
	String imageUrl,
	String thumbnailUrl,
	Boolean isWish
) {
}
