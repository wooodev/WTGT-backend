package com.batch.dto;

import java.util.Collections;
import java.util.List;

import lombok.Data;

@Data
public class DetailInfoResponse {
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
		private String contentid;
		private String contenttypeid;
		private String fldgubun;
		private String infoname;
		private String infotext;
		private String serialnum;
	}
}
