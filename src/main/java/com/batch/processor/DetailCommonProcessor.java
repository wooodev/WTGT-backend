package com.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.batch.dto.DetailCommonResponse;
import com.swyp.catsgotogedog.content.domain.entity.Content;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DetailCommonProcessor implements ItemProcessor<Content, Content> {

	private final RestClient restClient;
	private final String serviceKey;

	public DetailCommonProcessor(
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
	public Content process(Content content) throws Exception {
		log.info("{} ({}), 설명 정보 수집 중", content.getTitle(), content.getContentId());

		DetailCommonResponse response = restClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/detailCommon")
				.queryParam("serviceKey", serviceKey)
				.queryParam("MobileOS", "ETC")
				.queryParam("MobileApp", "Catsgotogedog")
				.queryParam("_type", "json")
				.queryParam("contentId", content.getContentId())
				.queryParam("overviewYN", "Y")
				.build())
			.retrieve()
			.body(DetailCommonResponse.class);

		DetailCommonResponse.Response bodyResponse = (response != null) ? response.response() : null;
		DetailCommonResponse.Body body = (bodyResponse != null) ? bodyResponse.body() : null;
		DetailCommonResponse.Items items = (body != null) ? body.items() : null;

		if (response != null && response.response().body().items() != null && !response.response().body().items().item().isEmpty()) {
			String overview = response.response().body().items().item().get(0).overview();
			if (overview == null || overview.isEmpty()) log.warn("{} ({}), 설명 정보가 없어 스킵됩니다.", content.getTitle(), content.getContentId());
			content.setOverview(overview);
		}
		return content;
	}
}
