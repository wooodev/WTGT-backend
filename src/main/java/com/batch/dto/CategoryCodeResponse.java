package com.batch.dto;

import java.util.Collections;
import java.util.List;

import lombok.Data;

@Data
public class CategoryCodeResponse {
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
		private String name;
		private String rnum;
		private String code;
		private String cat1;
		private String cat2;
		private String cat3;
	}
}
