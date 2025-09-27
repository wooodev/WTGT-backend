package com.swyp.catsgotogedog.aiplanner.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swyp.catsgotogedog.User.domain.entity.User;
import com.swyp.catsgotogedog.User.repository.UserRepository;
import com.swyp.catsgotogedog.aiplanner.domain.ContentEmbedding;
import com.swyp.catsgotogedog.aiplanner.repository.ContentEmbeddingRepository;
import com.swyp.catsgotogedog.aiplanner.request.PlannerRequest;
import com.swyp.catsgotogedog.aiplanner.response.AiplannerResponse;
import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.content.repository.ContentRepository;
import com.swyp.catsgotogedog.content.repository.RestaurantInformationRepository;
import com.swyp.catsgotogedog.content.repository.SightsInformationRepository;
import com.swyp.catsgotogedog.global.exception.CatsgotogedogException;
import com.swyp.catsgotogedog.global.exception.ErrorCode;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AiPlannerService {

	@Value("${clova.api.embed.url}")
	private String clovaUrl;

	@Value("${clova.api.key}")
	private String apiKey;

	private RestClient restClient;
	private final ContentEmbeddingRepository contentEmbeddingRepository;
	private final ContentRepository contentRepository;
	private final ElasticsearchClient esClient;
	private final ObjectMapper objectMapper;
	private final UserRepository userRepository;
	private final SightsInformationRepository sightsInformationRepository;
	private final RestaurantInformationRepository restaurantInformationRepository;

	public AiPlannerService(ContentEmbeddingRepository contentEmbeddingRepository, ContentRepository contentRepository, ElasticsearchClient esClient, ObjectMapper objectMapper, UserRepository userRepository, SightsInformationRepository sightsInformationRepository
	, RestaurantInformationRepository restaurantInformationRepository) {
		this.contentEmbeddingRepository = contentEmbeddingRepository;
		this.contentRepository = contentRepository;
		this.esClient = esClient;
		this.objectMapper = objectMapper;
		this.userRepository = userRepository;
		this.sightsInformationRepository = sightsInformationRepository;
		this.restaurantInformationRepository = restaurantInformationRepository;
	}

	@PostConstruct
	private void initRestClient() {
		this.restClient = RestClient.builder()
			.baseUrl(clovaUrl)
			.defaultHeader("Authorization", "Bearer " + apiKey)
			.defaultHeader("X-NCP-CLOVASTUDIO-REQUEST-ID", "f729bb278df149ce8b7e25e3ec8e1a25")
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.build();
	}

	@Transactional
	public void initEmbedContentData() {
		int processedCount = 0;
		int failedCount = 0;

		// Content 테이블 id 조회
		List<Content> allContents = contentRepository.findAll();

		// ContentId 추출
		List<Integer> allContentIds = allContents.stream()
			.map(Content::getContentId)
			.toList();

		// 현재 삽입되어있는 엘라스틱서치 Content 데이터 목록
		List<ContentEmbedding> existEmbeddings = contentEmbeddingRepository.findByContentIdIn(allContentIds);

		// 엘라스틱서치에 존재하는 Id 추출
		Set<Integer> existIds = existEmbeddings.stream()
			.map(ContentEmbedding::getContentId)
			.collect(Collectors.toSet());

		// 존재하지 않는 Content 데이터 추출
		List<Content> newContents = allContents.stream()
			.filter(content -> !existIds.contains(content.getContentId()))
			.filter(this::isValidEmbeddingData)
			.toList();

		List<ContentEmbedding> contentEmbeddings = new ArrayList<>();

		for(Content content : newContents) {
			try {
				if(processedCount > 0) {
					Thread.sleep(1000);
				}

				// 임베딩 텍스트 생성
				String textToEmbed = createEmbeddingText(content);
				// Clova 호출
				List<Float> embedding = callClovaEmbedApi(textToEmbed);

				if(!embedding.isEmpty()) {
					ContentEmbedding contentEmbedding = ContentEmbedding.builder()
						.contentId(content.getContentId())
						.title(content.getTitle())
						.contentTypeId(content.getContentTypeId())
						.categoryId(content.getCategoryId())
						.sidoCode(content.getSidoCode())
						.sigunguCode(content.getSigunguCode())
						.mapx(content.getMapx())
						.mapy(content.getMapy())
						.embedding(embedding)
						.build();

					contentEmbeddings.add(contentEmbedding);
					processedCount++;

					if(contentEmbeddings.size() >= 10) {
						saveToElasticSearch(contentEmbeddings);
						contentEmbeddings.clear();
					}

					if(processedCount % 10 == 0) {
						log.info("임베딩 진행 상황 : {}/{}", processedCount, newContents.size());
					}
				} else {
					failedCount++;
					log.warn("ContentId {} - 임베딩 생성 실패", content.getContentId());
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				log.error("임베딩 처리 중 인터럽트 발생", e);
				break;
			} catch(Exception e) {
				failedCount++;
				log.error("ContentId {} 임베딩 처리 중 오류발생 : {}", content.getContentId(), e.getMessage());
			}
		}




		log.info("임베딩 데이터 삽입 완료 - 성공 : {}, 실패 : {}", processedCount, failedCount);
	}

	public AiplannerResponse createPlan(String userId, PlannerRequest request) {
		validateUser(userId);

		List<Float> moodEmbed = getMoodEmbed(request.getMood());

		if(moodEmbed.isEmpty()) {
			throw new CatsgotogedogException(ErrorCode.CLOVA_HASHTAG_SERVER_ERROR);
		}

		List<AiplannerResponse.DayPlan> dayPlans = new ArrayList<>();

		int days = request.getDuration().getDays();
		Set<Integer> excludedIds = new HashSet<>();

		for(int day = 1; day <= days; day++) {
			AiplannerResponse.DayPlan dayPlan = createDayPlan(day, request.getSidoCode(), days, moodEmbed, excludedIds);
			dayPlans.add(dayPlan);
		}

		return new AiplannerResponse(dayPlans);
	}

	private AiplannerResponse.DayPlan createDayPlan(
		int day, int sido, int totalDays, List<Float> moodEmbed, Set<Integer> excludedIds
	) {
		List<AiplannerResponse.ContentInfo> dayContents = new ArrayList<>();

		dayContents.add(findBestPlace(sido, moodEmbed, 12, excludedIds));
		dayContents.add(findBestPlace(sido, moodEmbed, 39, excludedIds));
		dayContents.add(findBestPlace(sido, moodEmbed, 12, excludedIds));
		dayContents.add(findBestPlace(sido, moodEmbed, 39, excludedIds));

		if (totalDays > 1 && day < totalDays) {
			dayContents.add(findBestPlace(sido, moodEmbed, 32, excludedIds));
		}

		return new AiplannerResponse.DayPlan(day, dayContents);
	}

	private List<Float> getMoodEmbed(String mood) {

		Map<String, String> requestBody = Map.of("text", mood);

		try {
			log.info("{} 요청", clovaUrl);
			Map<String, Object> responseBody = Objects.requireNonNull(restClient.post()
				.body(requestBody)
				.retrieve()
				.body(Map.class));

			log.info("responseBody : {}", responseBody);

			Map<String, Object> result = (Map<String, Object>) responseBody.get("result");

			if(result != null) {
				Object embeddingObject = result.get("embedding");
				log.info(embeddingObject.toString());

				if (embeddingObject instanceof List) {
					List<Number> embeddingList = (List<Number>) embeddingObject;

					return embeddingList.stream()
						.map(Number::floatValue)
						.toList();
				}
			}

		} catch(RestClientResponseException e) {
			log.error("Clova Embed Api 호출 오류 :: 상태코드 {} - 응답 :: {}", e.getStatusCode(), e.getResponseBodyAsString());
			throw new CatsgotogedogException(ErrorCode.CLOVA_HASHTAG_SERVER_ERROR);
		} catch(Exception e) {
			log.error("Mood 임베드 Api 호출 중 예상치 못한 오류 발생", e);
			throw new CatsgotogedogException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		return Collections.emptyList();
	}

	private AiplannerResponse.ContentInfo findBestPlace(
		Integer sidoCode,
		List<Float> embededMood,
		Integer contentTypeId,
		Set<Integer> excludedIds) {

		try {
			List<Double> queryVector = embededMood.stream()
				.map(Float::doubleValue)
				.collect(Collectors.toList());

			SearchRequest searchRequest = SearchRequest.of(s -> s
				.index("content-embedding")
				.size(100)
				.query(q -> q
					.scriptScore(ss -> ss
						.query(query -> query
							.bool(b -> b
								.filter(f -> f.term(t -> t.field("sidoCode").value(sidoCode)))
								.filter(f -> f.term(t -> t.field("contentTypeId").value(contentTypeId)))
							)
						)
						.script(script -> script
							.source("cosineSimilarity(params.queryVector, 'embedding') + 1.0")
							.params("queryVector", JsonData.of(queryVector))
						)
					)
				)
			);

			SearchResponse<Map> response = esClient.search(searchRequest, Map.class);

			for(Hit<Map> hit : response.hits().hits()) {
				log.info("검색된 문서 ID: {}, Score: {}", hit.id(), hit.score());

				Map<String, Object> sourceAsMap = hit.source();

				if(sourceAsMap == null) {
					log.warn("sourceAsMap이 null입니다.");
					continue;
				}

				log.info("문서 내용: {}", sourceAsMap);

				Integer contentId = (Integer) sourceAsMap.get("contentId");
				log.info("contentId: {}, excludedIds에 포함여부: {}",
					contentId, excludedIds.contains(contentId));

				if(contentId != null && !excludedIds.contains(contentId)) {
					excludedIds.add(contentId);
					Content content = contentRepository.findByContentId(contentId);
					String rest = null;

					if(content.getContentTypeId() == 12) {
						rest = sightsInformationRepository.findRestDateByContentId(content.getContentId());
					} else if(content.getContentTypeId() == 39) {
						rest = restaurantInformationRepository.findRestDateByContentId(content.getContentId());
					}

					if(rest == null || rest.isEmpty()) {
						rest = null;
					}

					if(content == null) {
						log.warn("contentId {}에 해당하는 Content를 찾을 수 없습니다.", contentId);
						continue;
					}

					log.info("추천 장소 선택됨: {}", content.getTitle());

					return AiplannerResponse.ContentInfo.builder()
						.contentId(content.getContentId())
						.image(content.getImage())
						.thumbImage(content.getThumbImage())
						.title(content.getTitle())
						.addr1(content.getAddr1())
						.addr2(content.getAddr2())
						.mapx(content.getMapx())
						.mapy(content.getMapy())
						.categoryId(content.getCategoryId())
						.rest(rest)
						.build();
				}
			}

		} catch (Exception e) {
			log.error("Elasticsearch API 호출 오류 : {}", e.getMessage(), e);
			throw new CatsgotogedogException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		throw new CatsgotogedogException(ErrorCode.NO_RECOMMEND_PLACES);
	}

	private boolean isValidEmbeddingData(Content content) {
		boolean hasTitle = content.getTitle() != null && !content.getTitle().trim().isEmpty();
		boolean hasOverview = content.getOverview() != null && !content.getOverview().trim().isEmpty();

		if (!hasTitle || !hasOverview) {
			log.debug("ContentId {} - title 또는 overview가 모두 없어 임베딩 제외", content.getContentId());
			return false;
		}

		return true;
	}

	private String createEmbeddingText(Content content) {
		StringBuilder sb = new StringBuilder();
		if(content.getTitle() != null) {
			sb.append("제목 : ").append(content.getTitle()).append(" ");
		}
		if(content.getOverview() != null) {
			sb.append("소개 : ").append(content.getOverview()).append(" ");
		}
		return sb.toString().trim();
	}

	private List<Float> callClovaEmbedApi(String text) {
		Map<String, String> requestBody = Map.of("text", text);

		try {
			Map<String, Object> responseBody = Objects.requireNonNull(restClient.post()
				.body(requestBody)
				.retrieve()
				.body(Map.class));

			Map<String, Object> result = (Map<String, Object>) responseBody.get("result");

			if(result != null) {
				Object embeddingObject = result.get("embedding");
				log.info(embeddingObject.toString());

				if (embeddingObject instanceof List) {
					List<Number> embeddingList = (List<Number>) embeddingObject;

					return embeddingList.stream()
						.map(Number::floatValue)
						.toList();
				}
			}

		} catch(RestClientResponseException e) {
			log.error("Clova Embed Api 호출 오류 :: 상태코드 {} - 응답 :: {}", e.getStatusCode(), e.getResponseBodyAsString());
		} catch(Exception e) {
			log.error("{} Api 호출 중 예상치 못한 오류 발생", clovaUrl, e);
		}

		return Collections.emptyList();
	}

	private void saveToElasticSearch(List<ContentEmbedding> embeddings) {
		try {
			contentEmbeddingRepository.saveAll(embeddings);

			log.info("{} 개의 ContentEmbedding 데이터 저장 완료", embeddings.size());
		}catch (Exception e) {
			log.error("Elasticsearch 데이터 저장중 오류 발생 : ", e);
			throw new RuntimeException("Elasticsaerch 저장중 오류 발생", e);
		}
	}

	private User validateUser(String userId) {
		return userRepository.findById(Integer.parseInt(userId))
			.orElseThrow(() -> new CatsgotogedogException(ErrorCode.MEMBER_NOT_FOUND));
	}
}
