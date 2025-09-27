package com.batch.dto;

import java.util.Collections;
import java.util.List;

import com.batch.config.ItemsDeserializer;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.jsonwebtoken.io.IOException;

// 지역기반검색 (https://apis.data.go.kr/B551011/KorPetTourService/detailImage)
public record DetailImageResponse (Response response){

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

			// items 필드가 비어있을 경우
			if (node.isTextual() && node.asText().isEmpty()) {
				return new Items(Collections.emptyList());
			}

			// 정상적인 JSON 객체일 경우
			if (node.isObject()) {
				// "item"이라는 이름의 하위 노드를 찾습니다.
				JsonNode itemNode = node.get("item");

				// 하위 노드가 없을 경우
				if (itemNode == null || !itemNode.isArray()) {
					return new Items(Collections.emptyList());
				}

				// itemNode > List<Item>
				List<Item> itemList = mapper.convertValue(itemNode, new TypeReference<>() {});
				return new Items(itemList);
			}

			return new Items(Collections.emptyList());
		}

	}

	public record Item(
        String cpyrhtDivCd,
		String contentid,
		String imgname,
		String originimgurl,
		String serialnum,
		String smallimageurl
	){}
}
