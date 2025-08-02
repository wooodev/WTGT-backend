package com.batch.dto;

import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.io.IOException;

// 지역기반검색 (https://apis.data.go.kr/B551011/KorPetTourService/detailCommon)
public record DetailCommonResponse(Response response){

	public record Response(
		Header header,
		Body body
	) {}

	public record Header(
		String resultCode,
		String resultMsg
	) {}

	public record Body(
		Items items,
		int numOfRows,
		int pageNo,
		int totalCount
	) {}

	public record Items(List<Item> item) {

		@JsonCreator
		public static Items from(JsonNode node) throws IOException {

			final ObjectMapper mapper = new ObjectMapper();

			if (node.isTextual() && node.asText().isEmpty()) {
				return new Items(Collections.emptyList());
			}

			if (node.isObject()) {
				JsonNode itemNode = node.get("item");

				if (itemNode == null || !itemNode.isArray()) {
					return new Items(Collections.emptyList());
				}

				List<Item> itemList = mapper.convertValue(itemNode, new TypeReference<>() {});
				return new Items(itemList);
			}

			return new Items(Collections.emptyList());
		}

	}

	public record Item(
		String contentid,
		String contenttypeid,
		String overview
	){}
}
