package com.swyp.catsgotogedog.content.service;

import java.util.ArrayList;
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
		#ROLE
		당신은 관광지 정보를 분석하여 효과적인 해시태그를 생성하는 전문 AI입니다.
		#INSTRUCTION
		제공된 관광지의 제목과 내용을 분석하여 검색성과 마케팅 효과를 높이는 관련성 높은 해시태그를 생성합니다.
		
		#CONDITIONS
		1. 생성할 해시태그는 최소 5개, 최대 10개여야 합니다.
		2. 각 해시태그는 '#'으로 시작해야 합니다.
		3. 각 해시태그는 공백을 포함해서는 안 됩니다.
		4. 결과는 반드시 아래 <OUTPUT_FORMAT>에 명시된 JSON 형식이어야 하며, 다른 어떤 텍스트도 추가해서는 안 됩니다.
		
		#OUTPUT_FORMAT
		{
			"hashtags": [
				"#해시태그1",
				"#해시태그2",
				"...",
				"#해시태그10",
			]
		}
		# EXAMPLE
		- 입력:
		  - 제목: 제주의 숨겨진 보물, 비양도
		  - 내용: 제주 한림항에서 배를 타고 15분이면 도착하는 작은 섬 비양도. 아름다운 해안 산책로와 함께 여유로운 시간을 보낼 수 있는 곳입니다. 특히 일몰이 아름답기로 유명하며, 백패킹과 캠핑을 즐기는 사람들에게도 인기가 많습니다.
		
		- 출력:
		{
		  "hashtags": [
		    "#비양도",
		    "#제주도여행",
		    "#제주가볼만한곳",
		    "#섬여행",
		    "#제주숨은명소",
		    "#한림항",
		    "#제주일몰",
		    "#백패킹성지"
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
				if(hashtags.size() >= 5) {
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

						String content = rootNode
							.path("message")
							.path("content").asText();

						content = content.replaceAll("```json\\s*", "")
							.replaceAll("```\\s*", "")
							.trim();

						JsonNode contentNode = objectMapper.readTree(content);
						JsonNode hashtagArr = contentNode.path("hashtags");

						if(hashtagArr.isArray() && !hashtagArr.isEmpty()) {
							List<String> hashtags = new ArrayList<>();
							for(JsonNode hashtag : hashtagArr) {
								hashtags.add(hashtag.asText());
							}
							return hashtags;
						}

						return Collections.emptyList();
					}
				}
			}
			return Collections.emptyList();

		} catch(Exception e) {
			throw new RuntimeException("해시태그 파싱 오류", e);
		}
	}
}
