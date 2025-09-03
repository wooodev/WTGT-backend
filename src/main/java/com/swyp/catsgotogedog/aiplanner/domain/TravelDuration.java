package com.swyp.catsgotogedog.aiplanner.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TravelDuration {
	DAY_TRIP(1, "당일치기"),
	ONE_NIGHT(2, "1박2일"),
	TWO_NIGHT(3, "2박3일");

	private final int days;
	private final String description;
}
