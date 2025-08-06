package com.swyp.catsgotogedog.content.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.content.domain.entity.Hashtag;
import com.swyp.catsgotogedog.content.domain.request.ClovaApiRequest;
import com.swyp.catsgotogedog.content.repository.ContentRepository;
import com.swyp.catsgotogedog.content.repository.HashtagRepository;
import com.swyp.catsgotogedog.global.exception.CatsgotogedogException;
import com.swyp.catsgotogedog.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HashtagServcie {

	@Value("${clova.api.url}")
	private String apiUrl;

	@Value("${clova.api.key}")
	private String apiKey;

	@Value("${clova.api.request-id}")
	private String requestId;

	private final RestClient.Builder restClientBuilder;
	private final ObjectMapper objectMapper;

	private final ContentRepository contentRepository;
	private final HashtagRepository hashtagRepository;

	private static final String SYSTEM_PROMPT = """
		당신은 관광지 정보를 분석하여 효과적인 해시태그를 생성하는 전문 AI입니다.
		제공된 관광지의 제목과 내용을 분석하여 검색성과 마케팅 효과를 높이는 관련성 높은 해시태그를 생성합니다.
		형식:
		띄어쓰기 없이 연결하여 작성
		기호 포함하여 출력
		출력형식:
		{
		  "hashtags": [
			"#해시태그1",
			"#해시태그2",
			"...",
			"#해시태그10"
		  ]
		}
		""";

	@Transactional
	public void generateAndSaveHashTags(int contentId) {
		if(hashtagRepository.existsByContentId(contentId)) {
			return;
		}
		Content content = contentRepository.findById(contentId)
			.orElseThrow(() -> new CatsgotogedogException(ErrorCode.CONTENT_NOT_FOUND));

		try {
			List<String> hashtags = generateHashtags(content.getTitle(), content.getOverview());
			log.info("생성된 해시태그 :: {}", hashtags);

			if(hashtags != null && !hashtags.isEmpty()) {
				if(hashtags.size() > 5) {
				saveHashtags(contentId, hashtags);
				}
			}
		} catch (Exception e) {
			log.error("해시태그 생성중 오류 발생 : {}", contentId, e);
		}
	}

	private void saveHashtags(int contentId, List<String> hashtags) {
		List<Hashtag> hashLists = hashtags.stream()
			.map(tag -> {
				return Hashtag.builder()
					.contentId(contentId)
					.content(tag)
					.build();
			})
			.toList();

		hashtagRepository.saveAll(hashLists);
		log.info("해시태그 {}개 저장 완료 :: {}", hashtags.size(), contentId);
	}

	private List<String> generateHashtags(String title, String overview) {
		log.info("{} 의 해시태그 생성중", title);
		try {
			ClovaApiRequest request = createRequest(title, overview);

			RestClient restClient = restClientBuilder
				.baseUrl(apiUrl)
				.defaultHeader("Authorization", "Bearer " + apiKey)
				.defaultHeader("X-NCP-CLOVASTUDIO-REQUEST-ID", requestId)
				.defaultHeader("X-NCP-CLOVASTUDIO-REQUEST-ID", requestId)
				.defaultHeader("Accept", "text/event-stream")
				.build();

			String response = restClient.post()
				.body(request)
				.retrieve()
				.body(String.class);

			return parseHashtags(response);
		} catch(Exception e) {
			log.error("해시태그 API 요청 중 오류 발생", e);
			return null;
		}
	}


	private ClovaApiRequest createRequest(String title, String overview) {
		String userContent = String.format("제목: %s\n내용: %s",
			title != null ? title : "",
			overview != null ? overview.substring(0, Math.min(overview.length(), 500)) : "");

		ClovaApiRequest.Message.Content systemContent = new  ClovaApiRequest.Message.Content("text", SYSTEM_PROMPT);
		ClovaApiRequest.Message.Content userContentObj = new  ClovaApiRequest.Message.Content("text", userContent);

		ClovaApiRequest.Message systemMessage = new ClovaApiRequest.Message("system", List.of(systemContent));
		ClovaApiRequest.Message userMessage = new  ClovaApiRequest.Message("user", List.of(userContentObj));

		ClovaApiRequest request = new ClovaApiRequest();
		request.setMessages(List.of(systemMessage, userMessage));
		return request;
	}

	private List<String> parseHashtags(String response) {
		try {
			String[] lines = response.split("\n");

			for(int i = 0; i < lines.length; i++) {
				if(lines[i].trim().equals("event:result") && i + 1 < lines.length) {
					String dataLine = lines[i + 1];

					if(dataLine.startsWith("data:")) {
						String jsonData = dataLine.substring(5);
						JsonNode rootNode = objectMapper.readTree(jsonData);

						String contentJson = rootNode
							.path("message")
							.path("content").asText();

						JsonNode contentNode = objectMapper.readTree(contentJson);
						JsonNode hashtagArr = contentNode.path("hashtags");

						if(hashtagArr.isArray() && hashtagArr.size() > 0) {
							String hashtagString = hashtagArr.get(0).asText();

							return Arrays.stream(hashtagString.split(" "))
								.filter(tag -> tag.startsWith("#"))
								.limit(10)
								.toList();
						}
					}
				}
			}
			return Collections.emptyList();

		} catch(Exception e) {
			throw new RuntimeException("해시태그 파싱 오류", e);
		}
	}
}
