package com.batch.dto;

import java.util.Collections;
import java.util.List;

import lombok.Data;

// 반복정보검색 (https://apis.data.go.kr/B551011/KorPetTourService/detailInfo)
@Data
public class DetailInfoAccommodationResponse {
	private Response response;

	@Data
	public static class Response {
		private Header header;
		private Body body;
	}

	@Data
	public static class Header {
		private String resultCode;
		private String resultMsg;
	}

	@Data
	public static class Body {
		private Items items;
		private int numOfRows;
		private int pageNo;
		private int totalCount;
	}

	@Data
	public static class Items {
		private List<Item> item = Collections.emptyList();
	}

	@Data
	public static class Item {
		private String roominfono;
		private String roomtitle;
		private String roomsize1;
		private String roomcount;
		private String roombasecount;
		private String roommaxcount;
		private String roomoffseasonminfee1;
		private String roomoffseasonminfee2;
		private String roompeakseasonminfee1;
		private String roompeakseasonminfee2;
		private String roomintro;
		private String roombath;
		private String facility;
		private String roomhometheater;
		private String roomaircondition;
		private String roomtv;
		private String roompc;
		private String roomcable;
		private String roominternet;
		private String roomrefrigerator;
		private String roomtoiletries;
		private String roomsofa;
		private String roomcook;
		private String roomhairdryer;
		private String roomtable;
	}
}
