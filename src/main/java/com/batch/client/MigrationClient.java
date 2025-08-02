package com.batch.client;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.batch.dto.AreaBasedListResponse;
import com.batch.dto.CategoryCodeResponse;
import com.batch.dto.ContentTypeId;
import com.batch.dto.DetailImageResponse;
import com.batch.dto.DetailInfoResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MigrationClient {

	@Value("${tour.api.base-url}")
	private String baseUrl;

	@Value("${tour.api.service-key}")
	private String serviceKey;

	private RestClient restClient;

	public MigrationClient(RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.build();
	}

	private static void handle(HttpRequest request, ClientHttpResponse response) throws IOException {
		// 5xx 서버 에러 처리
		String errorBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
		log.error("에러 응답 areaBasedList API ({} {}): Status {}, Body: {}",
			request.getMethod(), request.getURI(), response.getStatusCode(), errorBody);
		throw new RuntimeException("AreaBasedList API 호출 오류 : " + errorBody);
	}

	// areaBasedList
	public AreaBasedListResponse getAreaBasedLists(int pageNo, int numOfRows, String contentTypeId, String modifiedTime) {
		UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(baseUrl + "/areaBasedList")
			.queryParam("serviceKey", serviceKey)
			.queryParam("pageNo", pageNo)
			.queryParam("numOfRows", numOfRows)
			.queryParam("MobileOS", "ETC")
			.queryParam("MobileApp", "CatsGoToGedog")
			.queryParam("contentTypeId", contentTypeId) // 관광타입(12:관광지, 14:문화시설, 15:축제공연행사, 28:레포츠, 32:숙박, 38:쇼핑, 39:음식점) ID
			.queryParam("_type", "json");

		if(modifiedTime != null && !modifiedTime.isEmpty()) {
			uri.queryParam("modifiedtime", modifiedTime);
		}
		URI fullUri = uri.encode(StandardCharsets.UTF_8).build().toUri();

		log.info("API Request URL (areaBasedList) : {}", uri.toUriString());

		try {
			return restClient.get()
				.uri(fullUri)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.onStatus(HttpStatus.BAD_REQUEST::equals, (request, response) -> {
					String errorBody = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
					log.error("에러 응답 areaBasedList API ({} {}): Status {}, Body {}",request.getMethod(), request.getURI(), response.getStatusCode(), errorBody);
				})
				.onStatus(HttpStatusCode::is4xxClientError, MigrationClient::handle)
				.onStatus(HttpStatusCode::is5xxServerError, MigrationClient::handle)
				.body(AreaBasedListResponse.class);
		}catch (RuntimeException e) {
			log.error("areaBasedList API 요청중 오류 발생 : {}", e.getMessage(), e);
			throw e; // 배치 스탭에서 재시도/스킵 위해 재throw
		}catch (Exception e) {// 기타 exception
			log.error("areaBasedList API 요청중 예상치 못한 오류 발생: {}", e.getMessage(), e);
			throw new RuntimeException("areaBasedList API 요청중 예상치 못한 오류 발생", e);
		}
	}

	public AreaBasedListResponse getAreaBasedLists(int pageNo, int numOfRows, String contentTypeId) {
		return getAreaBasedLists(pageNo, numOfRows, contentTypeId, null);
	}

	// categoryCode
	public List<CategoryCodeResponse.Item> getCategoryCode(String contentTypeId, String cat1, String cat2) {
		UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(baseUrl + "/categoryCode")
			.queryParam("serviceKey", serviceKey)
			.queryParam("pageNo", 1)
			.queryParam("numOfRows", 100)
			.queryParam("MobileOS", "ETC")
			.queryParam("MobileApp", "CatsGoToGedog")
			.queryParam("contentTypeId", contentTypeId) // 관광타입(12:관광지, 14:문화시설, 15:축제공연행사, 28:레포츠, 32:숙박, 38:쇼핑, 39:음식점) ID
			.queryParam("_type", "json");
		if(cat1 != null && !cat1.isEmpty()) {
			uri.queryParam("cat1", cat1);
		}
		if(cat2 != null && !cat2.isEmpty()) {
			uri.queryParam("cat2", cat2);
		}
		URI fullUri = uri.encode(StandardCharsets.UTF_8).build().toUri();

		try {
			CategoryCodeResponse response = restClient.get()
				.uri(fullUri)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.onStatus(HttpStatusCode::isError, MigrationClient::handle)
				.body(CategoryCodeResponse.class);
			return Optional.ofNullable(response)
				.map(CategoryCodeResponse::getResponse)
				.map(CategoryCodeResponse.Response::getBody)
				.map(CategoryCodeResponse.Body::getItems)
				.map(CategoryCodeResponse.Items::getItem)
				.orElse(Collections.emptyList());
		} catch (RuntimeException e) {
			log.error("categoryCode API 요청 중 오류 발생 (contentTypeId: {}, cat1: {}, cat2: {}): {}",
				contentTypeId, cat1, cat2, e.getMessage(), e);
			return Collections.emptyList();
		} catch (Exception e) {
			log.error("categoryCode API 요청 중 예상치 못한 오류 발생 (contentTypeId: {}, cat1: {}, cat2: {}): {}",
				contentTypeId, cat1, cat2, e.getMessage(), e);
			return Collections.emptyList();
		}
	}
}
