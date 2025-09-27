package com.swyp.catsgotogedog.review.domain.response;

public enum ReviewSortType {
	LATEST("c"),
	RECOMMENDED("r");

	private final String value;

	private ReviewSortType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
