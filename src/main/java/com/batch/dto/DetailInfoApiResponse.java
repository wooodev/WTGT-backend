package com.batch.dto;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.io.IOException;

// 지역기반검색 (https://apis.data.go.kr/B551011/KorPetTourService/detailInfo)
public record DetailInfoApiResponse(Response response){

	public record Response(Header header, Body body) {}
	public record Header(String resultCode, String resultMsg) {}

	public record Body(JsonNode items, int numOfRows, int pageNo, int totalCount) {}

	public record Items<T>(List<T> item) {}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record GeneralItem(String contentid, String contentypeid, String serialnum, String infoname, String infotext, String fldgubun) {}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record RoomItem(
		String contentid, String roomtitle, String roomsize1, String roomcount,
		String roombasecount, String roommaxcount, String roomintro,
		String roombathfacility, String roombath, String roomhometheater,
		String roomaircondition, String roomtv, String roompc, String roomcable,
		String roominternet, String roomrefrigerator, String roomtoiletries,
		String roomsofa, String roomcook, String roomtable, String roomhairdryer,
		String roomimg1, String roomimg1alt, String roomimg1cpyrhtdiv,
		String roomimg2, String roomimg2alt, String roomimg2cpyrhtdiv,
		String roomimg3, String roomimg3alt, String roomimg3cpyrhtdiv,
		String roomimg4, String roomimg4alt, String roomimg4cpyrhtdiv,
		String roomimg5, String roomimg5alt, String roomimg5cpyrhtdiv,
		String roomoffseasonminfee1, String roomoffseasonminfee2, String roompeakseasonminfee1,
		String roompeakseasonminfee2, String roomsize2
	) {}
}
