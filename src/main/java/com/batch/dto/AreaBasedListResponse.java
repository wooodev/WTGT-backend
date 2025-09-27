package com.batch.dto;

import java.util.List;

import com.batch.config.ItemsDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

// 지역기반검색 (https://apis.data.go.kr/B551011/KorPetTourService/areaBasedList)
public record AreaBasedListResponse (Response response) {

	public record Response(
		Header header,
		Body body
	) {}

	public record Header(
		String resultCode,
		String resultMsg
	) {}

	public record Body(
		@JsonDeserialize(using = ItemsDeserializer.class)
		Items items,
		int numOfRows,
		int pageNo,
		int totalCount
	) {}

	public record Items(List<Item> item) {}

	public record Item(
		String addr1,
		String addr2,
		String areacode,
		String cat1,
		String cat2,
		String cat3,
		String contentid,
		String contenttypeid,
		String createdtime,
		String firstimage,
		String firstimage2,
		String cpyrhtDivCd, // copyright
		String mapx,
		String mapy,
		String mlevel,
		String modifiedtime,
		String sigungucode,
		String tel,
		String title,
		String zipcode
	) {}
}
