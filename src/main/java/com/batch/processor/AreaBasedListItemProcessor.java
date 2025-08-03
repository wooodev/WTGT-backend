package com.batch.processor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.batch.dto.AreaBasedListResponse.Item;
import com.swyp.catsgotogedog.content.domain.entity.Content;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AreaBasedListItemProcessor implements ItemProcessor<Item, Content> {

	@Override
	public Content process(Item item) throws Exception {
		if (item == null) {
			log.warn("Null item received in AreaBasedListItemProcessor.");
			return null;
		}

		if(item.contentid() == null || item.contentid().isEmpty()) {
			log.warn("Content ID가 존재하지 않아 아이템 스킵 ContentId : {}", item.contentid());
			return null;
		}
		try {
			Content content = Content.builder()
				.contentId(Integer.parseInt(item.contentid()))
				.contentTypeId(Integer.parseInt(item.contenttypeid()))
				.categoryId(item.cat3())
				.title(item.title())
				.addr1(item.addr1())
				.addr2(item.addr2())
				.image(item.firstimage())
				.thumbImage(item.firstimage2())
				.mapx(Double.parseDouble(item.mapx()))
				.mapy(Double.parseDouble(item.mapy()))
				.mLevel(Integer.parseInt(item.mlevel().isEmpty() ? "0" : item.mlevel()))
				.tel(item.tel())
				.zipCode(encodeZipCode(item.zipcode()))
				.sigunguCode(Integer.parseInt(item.sigungucode().isEmpty() ? "0" : item.sigungucode()))
				.sidoCode(Integer.parseInt(item.areacode().isEmpty() ? "0" : item.areacode()))
				.copyright(item.cpyrhtDivCd())
				.createdAt(LocalDateTime.now())
				.modifiedAt(parseLocalDateTime(item.modifiedtime()))
				.build();
			return content;
		}catch (Exception e) {
			log.error("AreaBasedList item 처리중 오류가 발생했습니다 ({}) : {}", item.contentid(), e.getMessage(), e);
			return null;
		}
	}

	private LocalDateTime parseLocalDateTime(String dateStr) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		try {
			LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatter);
			return localDateTime;
		} catch(DateTimeParseException e) {
			log.warn("modified 데이터 파싱 실패 : {} - {} ", dateStr, e.getMessage());
			return LocalDateTime.now();
		}
	}

	private int encodeZipCode(String zipCode) {
		try {
			if(zipCode == null || zipCode.isEmpty()) {
				return 0;
			}
			if(zipCode.contains(",")) {
				return Integer.parseInt(zipCode.split(",")[0].replaceAll("^[0-9]", ""));
			} else {
				return Integer.parseInt(zipCode);
			}
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
