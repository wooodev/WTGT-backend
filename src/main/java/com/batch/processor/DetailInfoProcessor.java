package com.batch.processor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.batch.dto.DetailInfoApiResponse;
import com.batch.dto.DetailInfoProcessResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.content.domain.entity.batch.recur.RecurInformation;
import com.swyp.catsgotogedog.content.domain.entity.batch.recur.RecurInformationRoom;
import com.swyp.catsgotogedog.content.domain.entity.batch.recur.RecurInformationRoomImage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class DetailInfoProcessor implements ItemProcessor<Content, DetailInfoProcessResult> {

	private final RestClient restClient;
	private final String serviceKey;
	private final ObjectMapper objectMapper = new ObjectMapper();

	public DetailInfoProcessor(
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
	public DetailInfoProcessResult process(Content content) throws Exception {
		log.info("{} ({}, {}), 장소 반복 정보 수집 중", content.getTitle(), content.getContentId(), content.getContentTypeId());
		// DetailInfoApiResponse response = restClient.get()
		ResponseEntity<String> responseEntity = restClient.get()
			.uri(uriBuilder -> uriBuilder
				.path("/detailInfo")
				.queryParam("serviceKey", serviceKey)
				.queryParam("MobileOS", "ETC")
				.queryParam("MobileApp", "Catsgotogedog")
				.queryParam("contentId", content.getContentId())
				.queryParam("contentTypeId", content.getContentTypeId())
				.queryParam("_type", "json")
				.build()
			).retrieve()
			// .body(DetailInfoApiResponse.class);
			.toEntity(String.class);

		log.info("status :: {} ", responseEntity.getStatusCode());
		log.info("body :: {} ", responseEntity.getBody());

		DetailInfoApiResponse response = objectMapper.readValue(responseEntity.getBody(), DetailInfoApiResponse.class);

		if(response == null || response.response() == null || response.response().body() == null) {
			log.warn("{} ({}), 장소의 반복 정보가 없어 스킵됩니다.", content.getTitle(), content.getContentId());
			return new DetailInfoProcessResult(null, null);
		}

		JsonNode itemsNode = response.response().body().items();
		if(itemsNode == null || itemsNode.isEmpty()) {
			log.warn("{} ({}), ItemsNode 정보가 없어 스킵됩니다.", content.getTitle(), content.getContentId());
			return new DetailInfoProcessResult(null, null);
		}

		// 숙박
		if("32".equals(String.valueOf(content.getContentTypeId()))) {
			log.info("{} ({}), 숙소 반복 정보 데이터 삽입 준비중", content.getTitle(), content.getContentId());
			DetailInfoApiResponse.Items<DetailInfoApiResponse.RoomItem> items = objectMapper.convertValue(itemsNode, new TypeReference<>() {});

			if(items == null || items.item() == null || items.item().isEmpty()) {
				log.warn("{} ({}), 장소의 숙소 반복 정보가 없어 스킵됩니다.", content.getTitle(), content.getContentId());
				return new DetailInfoProcessResult(null, null);
			}

			List<RecurInformationRoom> rooms = items.item().stream().map(dto -> {
				RecurInformationRoom room = RecurInformationRoom.builder()
					.content(content)
					.roomTitle(dto.roomtitle())
					.roomSize1(Integer.valueOf(dto.roomsize1()))
					.roomCount(Integer.valueOf(dto.roomcount()))
					.roomBaseCount(Integer.valueOf(dto.roombasecount()))
					.roomMaxCount(Integer.valueOf(dto.roommaxcount()))
					.offSeasonWeekMinFee(Integer.valueOf(dto.roomoffseasonminfee1()))
					.offSeasonWeekendMinFee(Integer.valueOf(dto.roomoffseasonminfee2()))
					.peakSeasonWeekMinFee(Integer.valueOf(dto.roompeakseasonminfee1()))
					.peakSeasonWeekendMinFee(Integer.valueOf(dto.roompeakseasonminfee2()))
					.roomIntro(dto.roomintro())
					.roomBathFacility(dto.roombathfacility().equals("Y") ? Boolean.TRUE : Boolean.FALSE)
					.roomBath(dto.roombath().equals("Y") ? Boolean.TRUE : Boolean.FALSE)
					.roomHomeTheater(dto.roomhometheater().equals("Y") ? Boolean.TRUE : Boolean.FALSE)
					.roomAircondition(dto.roomaircondition().equals("Y") ? Boolean.TRUE : Boolean.FALSE)
					.roomTv(dto.roomtv().equals("Y") ? Boolean.TRUE : Boolean.FALSE)
					.roomPc(dto.roompc().equals("Y") ? Boolean.TRUE : Boolean.FALSE)
					.roomCable(dto.roomcable().equals("Y") ? Boolean.TRUE : Boolean.FALSE)
					.roomInternet(dto.roominternet().equals("Y") ? Boolean.TRUE : Boolean.FALSE)
					.roomRefrigerator(dto.roomrefrigerator().equals("Y") ? Boolean.TRUE : Boolean.FALSE)
					.roomToiletries(dto.roomtoiletries().equals("Y") ? Boolean.TRUE : Boolean.FALSE)
					.roomSofa(dto.roomsofa().equals("Y") ? Boolean.TRUE : Boolean.FALSE)
					.roomCook(dto.roomcook().equals("Y") ? Boolean.TRUE : Boolean.FALSE)
					.roomTable(dto.roomtable().equals("Y") ? Boolean.TRUE : Boolean.FALSE)
					.roomHairdryer(dto.roomhairdryer().equals("Y") ? Boolean.TRUE : Boolean.FALSE)
					.roomSize2(new BigDecimal(dto.roomsize2()))
					.images(new ArrayList<>()).build();
				addRoomImageIfPresent(room, dto.roomimg1(), dto.roomimg1alt(), dto.roomimg1cpyrhtdiv());
				addRoomImageIfPresent(room, dto.roomimg2(), dto.roomimg2alt(), dto.roomimg2cpyrhtdiv());
				addRoomImageIfPresent(room, dto.roomimg3(), dto.roomimg3alt(), dto.roomimg3cpyrhtdiv());
				addRoomImageIfPresent(room, dto.roomimg4(), dto.roomimg4alt(), dto.roomimg4cpyrhtdiv());
				addRoomImageIfPresent(room, dto.roomimg5(), dto.roomimg5alt(), dto.roomimg5cpyrhtdiv());
				return room;
			}).collect(Collectors.toList());
			return new DetailInfoProcessResult(null, rooms);
		} else {
			log.info("{} ({}), 일반 반복 정보 데이터 삽입 준비중", content.getTitle(), content.getContentId());
			DetailInfoApiResponse.Items<DetailInfoApiResponse.GeneralItem> items = objectMapper.convertValue(itemsNode, new TypeReference<>() {});
			if(items == null || items.item() == null || items.item().isEmpty()) {
				return new DetailInfoProcessResult(null, null);
			}
			List<RecurInformation> infos = items.item().stream()
				.map(dto -> RecurInformation.builder()
					.content(content)
					.infoName(dto.infoname())
					.infoText(dto.infotext())
					.build()
				).collect(Collectors.toList());
			return new DetailInfoProcessResult(infos, null);
		}
	}

	private void addRoomImageIfPresent(RecurInformationRoom room, String url, String alt, String copyright) {
		if(url != null && !url.trim().isEmpty()) {
			RecurInformationRoomImage image = RecurInformationRoomImage.builder()
				.room(room)
				.imageUrl(url)
				.imageAlt(alt)
				.imageCopyright(copyright)
				.build();
			room.getImages().add(image);
		}
	}
}
