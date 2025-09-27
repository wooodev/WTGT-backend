package com.swyp.catsgotogedog.review.domain.response;

import io.swagger.v3.oas.annotations.media.Schema;

public class ReportReasonResponse {
	@Schema(description = "신고 사유 ID")
	private int reasonId;

	@Schema(description = "신고 사유")
	private String content;
}
