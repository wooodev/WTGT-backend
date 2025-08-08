package com.swyp.catsgotogedog.content.domain.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ContentRankResponse {
	private int contentId;
	private String title;
	private String image;
	private String thumbImage;
	private int contentTypeId;
	private double mapx;
	private double mapy;
	private List<String> hashtag;
	private String categoryId;
	private double avgScore;
	private String restDate;
	private int ranking;
	private boolean wishData;
	private RegionCodeResponse regionName;
}
