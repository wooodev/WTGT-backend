package com.batch.processor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.batch.dto.DetailIntroProcessResult;
import com.batch.dto.DetailIntroResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.content.domain.entity.batch.information.FestivalInformation;
import com.swyp.catsgotogedog.content.domain.entity.batch.information.LodgeInformation;
import com.swyp.catsgotogedog.content.domain.entity.batch.information.RestaurantInformation;
import com.swyp.catsgotogedog.content.domain.entity.batch.information.SightsInformation;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DetailIntroProcessor implements ItemProcessor<Content, DetailIntroProcessResult> {

	private final RestClient restClient;
	private final String serviceKey;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public DetailIntroProcessor(
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
	public DetailIntroProcessResult process(Content content) throws Exception {
		log.info("{} ({}), 소개 정보 수집 중", content.getTitle(), content.getContentId());

		DetailIntroResponse response = restClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/detailIntro")
				.queryParam("serviceKey", serviceKey)
				.queryParam("MobileOS", "ETC")
				.queryParam("MobileApp", "Catsgotogedog")
				.queryParam("_type", "json")
				.queryParam("contentId", content.getContentId())
				.queryParam("contentTypeId", content.getContentTypeId())
				.build()
			)
			.retrieve()
			.body(DetailIntroResponse.class);

		if(response == null || response.response() == null || response.response().body() == null) {
			log.warn("{} ({}), 장소의 소개 정보가 없어 스킵됩니다.", content.getTitle(), content.getContentId());
			return new DetailIntroProcessResult(null, null, null, null);
		}

		JsonNode itemsNode = response.response().body().items();
		if(itemsNode == null || itemsNode.isEmpty()) {
			log.warn("{} ({}), ItemsNode 정보가 없어 스킵됩니다.", content.getTitle(), content.getContentId());
			return new DetailIntroProcessResult(null, null, null, null);
		}
		switch (content.getContentTypeId()) {
			case 12 -> {
				log.info("{} ({}), 관광지 소개 정보 데이터 삽입 준비중", content.getTitle(), content.getContentId());
				DetailIntroResponse.Items<DetailIntroResponse.SightsItem> items = objectMapper.convertValue(itemsNode, new TypeReference<>() {});

				List<SightsInformation> infos = items.item().stream()
					.map(dto -> SightsInformation.builder()
						.content(content)
						.contentTypeId(content.getContentTypeId())
						.accomCount(Integer.valueOf(dto.accomcount().replaceAll("[^0-9]", "")))
						.chkCreditcard(dto.chkcreditcard())
						.expAgeRange(dto.expagerange())
						.expGuide(dto.expguide())
						.infoCenter(dto.infocenter())
						.openDate(dto.opendate().isEmpty() ? null : LocalDate.parse(dto.opendate()))
						.parking(dto.parking())
						.restDate(dto.restdate())
						.useSeason(dto.useseason())
						.heritage1(Boolean.valueOf(dto.heritage1()))
						.heritage2(Boolean.valueOf(dto.heritage2()))
						.heritage3(Boolean.valueOf(dto.heritage3()))
						.build()
					)
					.collect(Collectors.toList());
				return new DetailIntroProcessResult(infos, null, null, null);
			}

			case 15 -> {
				log.info("{} ({}), 축제공연행사 소개 정보 데이터 삽입 준비중", content.getTitle(), content.getContentId());
				DetailIntroResponse.Items<DetailIntroResponse.FestivalItem> items = objectMapper.convertValue(itemsNode, new TypeReference<>() {});

				List<FestivalInformation> infos = items.item().stream()
					.map(dto -> FestivalInformation.builder()
						.content(content)
						.ageLimit(dto.agelimit())
						.bookingPlace(dto.bookingplace())
						.discountInfo(dto.discountinfofestival())
						.eventStartDate(LocalDate.parse(dto.eventstartdate()))
						.eventEndDate(LocalDate.parse(dto.eventenddate()))
						.eventHomepage(dto.eventhomepage())
						.eventPlace(dto.eventplace())
						.placeInfo(dto.placeinfo())
						.playTime(dto.playtime())
						.program(dto.program())
						.spendTime(dto.spendtimefestival())
						.organizer(dto.sponsor1())
						.organizerTel(dto.sponsor1tel())
						.supervisor(dto.sponsor2())
						.supervisorTel(dto.sponsor2tel())
						.subEvent(dto.subevent())
						.feeInfo(dto.usetimefestival())
						.build()
					)
					.collect(Collectors.toList());
				return new DetailIntroProcessResult(null, null, null, infos);
			}

			case 32 -> {
				log.info("{} ({}), 숙박 소개 정보 데이터 삽입 준비중", content.getTitle(), content.getContentId());
				DetailIntroResponse.Items<DetailIntroResponse.LodgeItem> items = objectMapper.convertValue(itemsNode, new TypeReference<>() {});

				List<LodgeInformation> infos = items.item().stream()
					.map(dto -> LodgeInformation.builder()
						.content(content)
						.capacityCount(Integer.valueOf(dto.accomcountlodging()))
						.benikia(Boolean.valueOf(dto.benikia()))
						.checkInTime(LocalTime.parse(dto.checkintime()))
						.checkOutTime(LocalTime.parse(dto.checkouttime()))
						.cooking(dto.chkcooking())
						.foodplace(dto.foodplace())
						.goodstay(Boolean.valueOf(dto.goodstay()))
						.hanok(Boolean.valueOf(dto.hanok()))
						.information(dto.infocenterlodging())
						.parking(dto.parkinglodging())
						.roomCount(Integer.valueOf(dto.roomcount()))
						.reservationInfo(dto.reservationlodging())
						.reservationUrl(dto.reservationurl())
						.roomType(dto.roomtype())
						.scale(dto.scalelodging())
						.subFacility(dto.subfacility())
						.barbecue(Boolean.valueOf(dto.barbecue()))
						.beauty(Boolean.valueOf(dto.beauty()))
						.beverage(Boolean.valueOf(dto.beverage()))
						.bicycle(Boolean.valueOf(dto.bicycle()))
						.campfire(Boolean.valueOf(dto.campfire()))
						.fitness(Boolean.valueOf(dto.fitness()))
						.karaoke(Boolean.valueOf(dto.karaoke()))
						.publicBath(Boolean.valueOf(dto.publicbath()))
						.publicPcRoom(Boolean.valueOf(dto.publicpc()))
						.sauna(Boolean.valueOf(dto.sauna()))
						.seminar(Boolean.valueOf(dto.seminar()))
						.sports(Boolean.valueOf(dto.sports()))
						.refundRegulation(dto.refundregulation())
						.build()
					)
					.collect(Collectors.toList());
				return new DetailIntroProcessResult(null, infos, null, null);
			}

			case 39 -> {
				log.info("{} ({}), 음식점 소개 정보 데이터 삽입 준비중", content.getTitle(), content.getContentId());
				DetailIntroResponse.Items<DetailIntroResponse.RestaurantItem> items = objectMapper.convertValue(itemsNode, new TypeReference<>() {});

				List<RestaurantInformation> infos = items.item().stream()
					.map(dto -> RestaurantInformation.builder()
							.content(content)
							.chkCreditcard(dto.chkcreditcardfood())
							.discountInfo(dto.discountinfofood())
							.signatureMenu(dto.firstmenu())
							.information(dto.infocenterfood())
							.kidsFacility(Boolean.valueOf(dto.kidsfacility()))
							.openDate(LocalDate.parse(dto.opendatefood()))
							.openTime(dto.opentimefood())
							.parking(dto.parkingfood())
							.reservation(dto.reservationfood())
							.scale(Integer.valueOf(dto.scalefood()))
							.smoking(Boolean.valueOf(dto.smoking()))
							.treatMenu(dto.treatmenu())
							.build()
						)
					.collect(Collectors.toList());
				return new DetailIntroProcessResult(null, null, infos, null);
			}

		}
		return null;
	}
}
