package com.swyp.catsgotogedog.content.domain.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HashtagClovaResponse {
	private List<String> hashtags;
}
