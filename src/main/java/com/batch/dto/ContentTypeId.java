package com.batch.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ContentTypeId {
	관광지(12),
	문화시설(14),
	축제공연행사(15),
	레포츠(28),
	숙박(32),
	쇼핑(38),
	음식점(39);

	private final int contentTypeId;
}
