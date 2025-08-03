package com.batch.processor;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.batch.dto.DetailImageResponse;
import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.content.domain.entity.ContentImage;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DetailImageProcessor implements ItemProcessor<Content, List<ContentImage>> {

	private final RestClient restClient;
	private final String serviceKey;

	public DetailImageProcessor(
		RestClient.Builder restClientBuilder,
		@Value("${tour.api.base-url}") String baseUrl,
		@Value("${tour.api.service-key}") String serviceKey
	) {
		this.restClient = restClientBuilder
			.baseUrl(baseUrl)
			.build();
		this.serviceKey = serviceKey;
	}

	@Override
	public List<ContentImage> process(Content content) throws Exception {
		log.info("{} ({}), 이미지 정보 수집 중", content.getTitle(), content.getContentId());

		DetailImageResponse response = restClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/detailImage")
				.queryParam("serviceKey", serviceKey)
				.queryParam("MobileOS", "ETC")
				.queryParam("MobileApp", "Catsgotogedog")
				.queryParam("_type", "json")
				.queryParam("contentId", content.getContentId())
				.build()
			)
			.retrieve()
			.body(DetailImageResponse.class);

		DetailImageResponse.Response bodyResponse = (response != null) ? response.response() : null;
		DetailImageResponse.Body body = (bodyResponse != null) ? bodyResponse.body() : null;
		DetailImageResponse.Items items = (body != null) ? body.items() : null;

		if (items == null || items.item() == null || items.item().isEmpty()) {
			log.warn("{} ({}), 이미지 정보가 없어 스킵됩니다.", content.getTitle(), content.getContentId());
			return Collections.emptyList(); // 이미지가 없으면 빈 리스트 반환
		}

		return response.response().body().items().item().stream()
			.map(item -> {
				return ContentImage.builder()
					.content(content)
					.imageUrl(item.originimgurl())
					.smallImageUrl(item.smallimageurl())
					.build();
			}).collect(Collectors.toList());
	}
}
