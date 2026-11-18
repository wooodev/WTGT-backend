package com.batch.processor;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
		ResponseEntity<String> responseEntity = restClient.get()
				.uri(uriBuilder -> {
							URI uri = uriBuilder
									.path("/detailIntro2")
									.queryParam("serviceKey", "{serviceKey}")
									.queryParam("MobileOS", "ETC")
									.queryParam("MobileApp", "Catsgotogedog")
									.queryParam("contentId", content.getContentId())
									.queryParam("contentTypeId", content.getContentTypeId())
									.queryParam("_type", "json")
									.build(serviceKey);
							log.debug("소개정보 요청 contentId={}", content.getContentId());
							return uri;
						}
				)
				.retrieve()
				.toEntity(String.class);

		if(responseEntity.getBody() != null && responseEntity.getBody().contains("LIMITED_NUMBER_OF_SERVICE_REQUESTS_EXCEEDS_ERROR")) {
			log.warn("DetailIntroProcessor API 호출 한도 도달. 아이템 스킵");
			return null;
		}

		DetailIntroResponse response = objectMapper.readValue(responseEntity.getBody(), DetailIntroResponse.class);

		if(response == null || response.response() == null || response.response().body() == null) {
			log.warn("{} ({}), 장소의 소개 정보가 없어 스킵됩니다.", content.getTitle(), content.getContentId());
			return null;
		}

		JsonNode itemsNode = response.response().body().items().get("item");
		if(itemsNode == null || itemsNode.isEmpty()) {
			log.warn("{} ({}), ItemsNode 정보가 없어 스킵됩니다.", content.getTitle(), content.getContentId());
			return null;
		}

		log.info("itemsNode :: {}",  itemsNode);

		switch (content.getContentTypeId()) {
			case 12 -> {
				log.info("{} ({}), 관광지 소개 정보 데이터 삽입 준비중", content.getTitle(), content.getContentId());
				//DetailIntroResponse.Items<DetailIntroResponse.SightsItem> items = objectMapper.convertValue(itemsNode, new TypeReference<>() {});

				List<DetailIntroResponse.SightsItem> sightsItems = objectMapper.convertValue(
						itemsNode,
						new TypeReference<List<DetailIntroResponse.SightsItem>>() {}
				);

				DetailIntroResponse.Items<DetailIntroResponse.SightsItem> items =
						new DetailIntroResponse.Items<>(sightsItems);

				List<SightsInformation> infos = items.item().stream()
						.map(dto -> SightsInformation.builder()
								.content(content)
								.contentTypeId(content.getContentTypeId())
								.accomCount(parseAccom(dto.accomcount()))
								.chkCreditcard(dto.chkcreditcard())
								.expAgeRange(dto.expagerange())
								.expGuide(dto.expguide())
								.infoCenter(dto.infocenter())
								.openDate(parseDate(dto.opendate(), 12))
								.parking(dto.parking())
								.restDate(dto.restdate())
								.useSeason(dto.useseason())
								.useTime(dto.usetime())
								.heritage1("1".equals(dto.heritage1()))
								.heritage2("1".equals(dto.heritage2()))
								.heritage3("1".equals(dto.heritage3()))
								.build()
						)
						.collect(Collectors.toList());
				log.info("infos :: {}",  infos);
				return new DetailIntroProcessResult(infos, null, null, null);
			}

			case 15 -> {
				log.info("{} ({}), 축제공연행사 소개 정보 데이터 삽입 준비중", content.getTitle(), content.getContentId());
				//DetailIntroResponse.Items<DetailIntroResponse.FestivalItem> items = objectMapper.convertValue(itemsNode, new TypeReference<>() {});

				List<DetailIntroResponse.FestivalItem> festivalItems = objectMapper.convertValue(
						itemsNode,
						new TypeReference<List<DetailIntroResponse.FestivalItem>>() {}
				);

				DetailIntroResponse.Items<DetailIntroResponse.FestivalItem> items =
						new DetailIntroResponse.Items<>(festivalItems);

				List<FestivalInformation> infos = items.item().stream()
						.map(dto -> FestivalInformation.builder()
								.content(content)
								.ageLimit(dto.agelimit())
								.bookingPlace(dto.bookingplace())
								.discountInfo(dto.discountinfofestival())
								.eventStartDate(parseDate(dto.eventstartdate(), 15))
								.eventEndDate(parseDate(dto.eventenddate(), 15))
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
				//DetailIntroResponse.Items<DetailIntroResponse.LodgeItem> items = objectMapper.convertValue(itemsNode, new TypeReference<>() {});

				List<DetailIntroResponse.LodgeItem> lodgeItems = objectMapper.convertValue(
						itemsNode,
						new TypeReference<List<DetailIntroResponse.LodgeItem>>() {}
				);

				DetailIntroResponse.Items<DetailIntroResponse.LodgeItem> items =
						new DetailIntroResponse.Items<>(lodgeItems);

				List<LodgeInformation> infos = items.item().stream()
						.map(dto -> LodgeInformation.builder()
								.content(content)
								.capacityCount(parseAccom(dto.accomcountlodging()))
								.benikia("1".equals(dto.benikia()))
								.checkInTime(dto.checkintime())
								.checkOutTime(dto.checkouttime())
								.cooking(dto.chkcooking())
								.foodplace(dto.foodplace())
								.pickupService(dto.pickup())
								.goodstay("1".equals(dto.goodstay()))
								.hanok("1".equals(dto.hanok()))
								.information(dto.infocenterlodging())
								.parking(dto.parkinglodging())
								.roomCount(parseAccom(dto.roomcount()))
								.reservationInfo(dto.reservationlodging())
								.reservationUrl(dto.reservationurl())
								.roomType(dto.roomtype())
								.scale(dto.scalelodging())
								.subFacility(dto.subfacility())
								.barbecue("1".equals(dto.barbecue()))
								.beauty("1".equals(dto.beauty()))
								.beverage("1".equals(dto.beverage()))
								.bicycle("1".equals(dto.bicycle()))
								.campfire("1".equals(dto.campfire()))
								.fitness("1".equals(dto.fitness()))
								.karaoke("1".equals(dto.karaoke()))
								.publicBath("1".equals(dto.publicbath()))
								.publicPcRoom("1".equals(dto.publicpc()))
								.sauna("1".equals(dto.sauna()))
								.seminar("1".equals(dto.seminar()))
								.sports("1".equals(dto.sports()))
								.refundRegulation(dto.refundregulation())
								.build()
						)
						.collect(Collectors.toList());
				return new DetailIntroProcessResult(null, infos, null, null);
			}

			case 39 -> {
				log.info("{} ({}), 음식점 소개 정보 데이터 삽입 준비중", content.getTitle(), content.getContentId());
				//DetailIntroResponse.Items<DetailIntroResponse.RestaurantItem> items = objectMapper.convertValue(itemsNode, new TypeReference<>() {});

				List<DetailIntroResponse.RestaurantItem> restaurantItems = objectMapper.convertValue(
						itemsNode,
						new TypeReference<List<DetailIntroResponse.RestaurantItem>>() {}
				);

				DetailIntroResponse.Items<DetailIntroResponse.RestaurantItem> items =
						new DetailIntroResponse.Items<>(restaurantItems);

				List<RestaurantInformation> infos = items.item().stream()
						.map(dto -> RestaurantInformation.builder()
								.content(content)
								.chkCreditcard(dto.chkcreditcardfood())
								.discountInfo(dto.discountinfofood())
								.signatureMenu(dto.firstmenu())
								.information(dto.infocenterfood())
								.kidsFacility("1".equals(dto.kidsfacility()))
								.openDate(parseDate(dto.opendatefood(), 39))
								.openTime(dto.opentimefood())
								.parking(dto.parkingfood())
								.reservation(dto.reservationfood())
								.scale(dto.scalefood().isEmpty() ? 0 : Integer.parseInt(dto.scalefood()))
								.smoking("1".equals(dto.smoking()))
								.treatMenu(dto.treatmenu())
								.restDate(dto.restdatefood())
								.seat(dto.seat())
								.build()
						)
						.collect(Collectors.toList());
				return new DetailIntroProcessResult(null, null, infos, null);
			}

		}
		return null;
	}

	private LocalDate parseDate(String dateStr, int contentType) {
		if(dateStr == null || dateStr.isEmpty()) {
			return null;
		}
		try {
			switch (contentType) {
				case 15 -> {
					return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
				}
				default -> {
					return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				}
			}
		} catch (DateTimeParseException e) {
			return null;
		}
	}

	private int parseAccom(String accom) {
		String replaceAccom = accom.replaceAll("[^0-9]", "");
		return replaceAccom.isEmpty() ? 0 : Integer.parseInt(replaceAccom);
	}
}
