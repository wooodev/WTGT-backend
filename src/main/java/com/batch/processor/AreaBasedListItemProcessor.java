package com.batch.processor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Component;

import com.batch.dto.AreaBasedListResponse.Item;
import com.swyp.catsgotogedog.content.domain.entity.Content;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AreaBasedListItemProcessor implements ItemProcessor<Item, Content> {

	// 인천 제물포구(옛 중구 일부 + 동구). 우편번호로 옛 구를 다시 판단한다.
	private static final String JEMULPO_KEY = "28-125";
	private static final int INCHEON_SIDO = 2;
	private static final int OLD_JUNG_GU = 10;
	private static final int OLD_DONG_GU = 5;

	private final JdbcTemplate jdbcTemplate;

	// "법정동시도-법정동시군구" -> {옛 sido_code, 옛 sigungu_code}
	private volatile Map<String, int[]> regionMapping;

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
			int zipCode = encodeZipCode(item.zipcode());
			int[] region = resolveRegion(item, zipCode);

			Content content = Content.builder()
					.contentId(Integer.parseInt(item.contentid()))
					.contentTypeId(Integer.parseInt(item.contenttypeid()))
					.categoryId(resolveCategoryId(item))
					.title(item.title())
					.addr1(item.addr1())
					.addr2(item.addr2())
					.image(item.firstimage())
					.thumbImage(item.firstimage2())
					.mapx(Double.parseDouble(item.mapx()))
					.mapy(Double.parseDouble(item.mapy()))
					.mLevel(Integer.parseInt(item.mlevel().isEmpty() ? "0" : item.mlevel()))
					.tel(item.tel())
					.zipCode(zipCode)
					.sigunguCode(region[1])
					.sidoCode(region[0])
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

	// 옛 cat3가 있으면 그대로, 없으면 v2 분류(lclsSystm3) 사용
	private String resolveCategoryId(Item item) {
		if (item.cat3() != null && !item.cat3().isEmpty()) {
			return item.cat3();
		}
		return item.lclsSystm3();
	}

	// 반환값: {sido_code, sigungu_code} (기존 region_code 기준)
	private int[] resolveRegion(Item item, int zipCode) {
		// 옛 형식 응답이면 그대로 사용
		if (item.areacode() != null && !item.areacode().isEmpty()) {
			int sido = Integer.parseInt(item.areacode());
			int sigungu = (item.sigungucode() == null || item.sigungucode().isEmpty())
					? 0 : Integer.parseInt(item.sigungucode());
			return new int[] {sido, sigungu};
		}

		String key = item.lDongRegnCd() + "-" + item.lDongSignguCd();

		if (JEMULPO_KEY.equals(key)) {
			if (zipCode >= 22300 && zipCode <= 22499) {
				return new int[] {INCHEON_SIDO, OLD_JUNG_GU};
			}
			if (zipCode >= 22500 && zipCode <= 22599) {
				return new int[] {INCHEON_SIDO, OLD_DONG_GU};
			}
			log.warn("제물포구 우편번호로 옛 구를 판단할 수 없어 기본값(동구) 사용 - contentId: {}, zipcode: {}",
					item.contentid(), item.zipcode());
		}

		int[] region = getRegionMapping().get(key);
		if (region == null) {
			log.warn("지역 매핑 없음 - contentId: {}, lDongRegnCd: {}, lDongSignguCd: {}",
					item.contentid(), item.lDongRegnCd(), item.lDongSignguCd());
			return new int[] {0, 0};
		}
		return region;
	}

	// region_code_mapping을 처음 한 번만 읽어서 메모리에 보관
	private Map<String, int[]> getRegionMapping() {
		Map<String, int[]> mapping = regionMapping;
		if (mapping == null) {
			synchronized (this) {
				mapping = regionMapping;
				if (mapping == null) {
					Map<String, int[]> loaded = new HashMap<>();
					jdbcTemplate.query(
							"SELECT ldong_regn_cd, ldong_signgu_cd, sido_code, sigungu_code FROM region_code_mapping",
							(RowCallbackHandler) rs -> {
								loaded.put(rs.getString(1) + "-" + rs.getString(2),
										new int[] {rs.getInt(3), rs.getInt(4)});
							});
					log.info("region_code_mapping {}건 로드", loaded.size());
					regionMapping = mapping = loaded;
				}
			}
		}
		return mapping;
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
				return Integer.parseInt(zipCode.split(",")[0].replaceAll("[^0-9]", ""));
			} else {
				return Integer.parseInt(zipCode);
			}
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}
