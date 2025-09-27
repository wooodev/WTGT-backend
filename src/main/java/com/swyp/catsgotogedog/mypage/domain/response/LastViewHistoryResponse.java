package com.swyp.catsgotogedog.mypage.domain.response;

public record LastViewHistoryResponse (
	Integer contentId,
	String contentTitle,
	String imageUrl,
	String thumbnailUrl,
	Boolean isWish
) {
	public LastViewHistoryResponse(
		Integer contentId,
		String contentTitle,
		String imageUrl,
		String thumbnailUrl,
		Boolean isWish) {
		this.contentId = contentId;
		this.contentTitle = contentTitle;
		this.imageUrl = imageUrl;
		this.thumbnailUrl = thumbnailUrl;
		this.isWish = isWish;
	}
}
