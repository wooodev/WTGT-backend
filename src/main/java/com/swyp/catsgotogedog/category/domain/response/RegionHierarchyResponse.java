package com.swyp.catsgotogedog.category.domain.response;

import java.util.List;
import java.util.stream.Collectors;

import com.swyp.catsgotogedog.content.domain.entity.RegionCode;


public record RegionHierarchyResponse (
	int sidoCode,
	String regionName,
	List<SubRegionResponse> subRegions
) {
	public RegionHierarchyResponse(RegionCode regionCode, List<RegionCode> subRegions) {
		this(
			regionCode.getSidoCode(),
			regionCode.getRegionName(),
			subRegions.stream().map(SubRegionResponse::new).collect(Collectors.toList())
		);
	}
}
