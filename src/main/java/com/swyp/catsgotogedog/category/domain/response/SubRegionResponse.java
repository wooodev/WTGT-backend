package com.swyp.catsgotogedog.category.domain.response;

import com.swyp.catsgotogedog.content.domain.entity.RegionCode;

public record SubRegionResponse(
	int sigunguCode,
	String regionName
){
	public SubRegionResponse(RegionCode regionCode) {
		this(regionCode.getSigunguCode(), regionCode.getRegionName());
	}
}