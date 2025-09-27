package com.swyp.catsgotogedog.content.domain.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClovaGenerationRequest {
	private String title;
	private String content;
}
