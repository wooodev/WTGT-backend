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

			// ObjectMapper는 한 번만 생성하는 것이 효율적입니다.
			final ObjectMapper mapper = new ObjectMapper();

			// 1. items 필드가 비어있는 문자열("")일 경우
			if (node.isTextual() && node.asText().isEmpty()) {
				return new Items(Collections.emptyList());
			}

			// 2. 정상적인 JSON 객체일 경우
			if (node.isObject()) {
				// "item"이라는 이름의 하위 노드를 찾습니다.
				JsonNode itemNode = node.get("item");

				// 하위 노드가 없거나 배열이 아니면 빈 리스트로 처리합니다.
				if (itemNode == null || !itemNode.isArray()) {
					return new Items(Collections.emptyList());
				}

				// ✨ itemNode(배열)를 List<Item> 타입으로 직접 변환합니다.
				List<Item> itemList = mapper.convertValue(itemNode, new TypeReference<>() {});
				return new Items(itemList);
			}

			// 3. 그 외의 모든 경우 (null 등)
			return new Items(Collections.emptyList());
		}

	}

	public record Item(
		String contentid,
		String contenttypeid,
		String overview
	){}
}
