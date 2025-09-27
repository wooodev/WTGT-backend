package com.swyp.catsgotogedog.review.domain.response;

import com.swyp.catsgotogedog.review.domain.entity.ReviewImage;

public record ReviewImageResponse (
	int imageId, String imageUrl
) {
	public static ReviewImageResponse from(ReviewImage reviewImage) {
		return new ReviewImageResponse(
			reviewImage.getImageId(),
			reviewImage.getImageUrl()
		);
	}
}
