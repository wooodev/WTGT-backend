package com.swyp.catsgotogedog.aiplanner.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
public class AiplannerResponse {
	private List<DayPlan> dayPlans;

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class DayPlan {
		private Integer day;
		private List<ContentInfo> dayContents;
	}

	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	@Builder
	public static class ContentInfo {
		private Integer contentId;
		private String title;
		private String categoryId;
		private String addr1;
		private String addr2;
		private String image;
		private String thumbImage;
		private double mapx;
		private double mapy;
		private String rest;
	}
}
