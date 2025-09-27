package com.swyp.catsgotogedog.content.domain.request;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClovaApiRequest {
	private List<Message> messages;
	private double topP = 0.8;
	private int topK = 40;
	private int maxTokens = 256;
	private double temperature = 0.3;
	private double repetitionPenalty = 1.1;
	private List<String> stop = new ArrayList<>();
	private int seed = 0;
	private boolean includeAiFilters = true;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Message {
		private String role;
		private List<Content> content;

		@Data
		@NoArgsConstructor
		@AllArgsConstructor
		public static class Content {
			private String type;
			private String text;
		}
	}
}
