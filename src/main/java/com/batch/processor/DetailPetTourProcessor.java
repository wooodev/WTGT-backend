package com.batch.processor;

import java.util.Collections;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.batch.dto.DetailImageResponse;
import com.batch.dto.DetailPetTourResponse;
import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.pet.domain.entity.PetGuide;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DetailPetTourProcessor implements ItemProcessor<Content, PetGuide> {

	private final RestClient restClient;
	private final String serviceKey;

	public DetailPetTourProcessor(
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
	public PetGuide process(Content content) throws Exception {
		log.info("{} ({}), 반려동물 이용 가이드 수집 중", content.getTitle(), content.getContentId());

		DetailPetTourResponse response = restClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/detailPetTour")
				.queryParam("serviceKey", serviceKey)
				.queryParam("MobileOS", "ETC")
				.queryParam("MobileApp", "Catsgotogedog")
				.queryParam("_type", "json")
				.queryParam("contentId", content.getContentId())
				.build()
			)
			.retrieve()
			.body(DetailPetTourResponse.class);

		DetailPetTourResponse.Response bodyResponse = (response != null) ? response.response() : null;
		DetailPetTourResponse.Body body = (bodyResponse != null) ? bodyResponse.body() : null;
		DetailPetTourResponse.Items items = (body != null) ? body.items() : null;

		if (response == null || response.response().body().items() == null || response.response().body().items().item() == null) {
			log.info("{} ({}), 반려동물 이용 가이드 정보가 없어 스킵됩니다.", content.getTitle(), content.getContentId());
			return null;
		}

		DetailPetTourResponse.Item item = response.response().body().items().item().get(0);

		if(item.acmpyPsblCpam().equals("불가능")) {
			log.info("{} ({}) 반려동물 동반 불가능하여 스킵", content.getTitle(), content.getContentId());
			return null;
		}

		return PetGuide.builder()
			.content(content)
			.petPrep(item.acmpyNeedMtr()) //동반시 준비물
			.accidentPrep(item.relaAcdntRiskMtr()) // 사고 대비사항
			.allowedPetType(item.acmpyPsblCpam()) // 동반가능 동물
			.availableFacility(item.relaPosesFclty()) // 보유시설
			.providedItem(item.relaFrnshPrdlst()) // 비치품목
			.etcInfo(item.etcAcmpyInfo()) // 기타 동반 정보
			.purchasableItem(item.relaPurcPrdlst()) // 구매가능 품목
			.withPet(item.acmpyTypeCd()) // 동반 가능 구역
			.rentItem(item.relaRntlPrdlst()) // 대여가능 품목
			.build();
	}
}
