package com.batch.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;

// 지역기반검색 (https://apis.data.go.kr/B551011/KorPetTourService/detailIntro)
public record DetailIntroResponse(Response response){

	public record Response(Header header, Body body) {}
	public record Header(String resultCode, String resultMsg) {}

	public record Body(JsonNode items, int numOfRows, int pageNo, int totalCount) {}

	public record Items<T>(List<T> item) {}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record SightsItem(
		String contentid,		String contenttypeid,
		String heritage1,		String heritage2,
		String heritage3,		String infocenter,
		String opendate,		String restdate,
		String expguide,		String expagerange,
		String accomcount,		String useseason,
		String usetime,			String parking,
		String chkcreditcard
	) {}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record FestivalItem(
		String contentid,			String contenttypeid,
		String sponsor1,			String sponsor1tel,
		String sponsor2,			String sponsor2tel,
		String eventenddate,		String playtime,
		String eventplace,			String eventhomepage,
		String agelimit,			String bookingplace,
		String placeinfo,			String subevent,
		String program,				String eventstartdate,
		String usetimefestival,		String discountinfofestival,
		String spendtimefestival,	String festivalgrade
	) {}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record LodgeItem(
		String contentid,		String contenttypeid,
		String goodstay,		String benikia,
		String hanok,			String roomcount,
		String roomtype,		String checkintime,
		String checkouttime,	String chkcooking,
		String seminar,			String sports,
		String sauna,			String beauty,
		String beverage,		String karaoke,
		String barbecue,		String campfire,
		String bicycle,			String fitness,
		String publicpc,		String publicbath,
		String subfacility,		String foodplace,
		String pickup,			String infocenterlodging,
		String parkinglodging,	String reservationlodging,
		String scalelodging,	String accomcountlodging,
		String reservationurl,	String refundregulation
	) {}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record RestaurantItem(
		String contentid,			String contenttypeid,
		String seat,				String kidsfacility,
		String firstmenu,			String treatmenu,
		String smoking,				String packing,
		String infocenterfood,		String scalefood,
		String parkingfood,			String opendatefood,
		String opentimefood,		String restdatefood,
		String discountinfofood,	String chkcreditcardfood,
		String reservationfood,		String lcnsno
	) {}
}
