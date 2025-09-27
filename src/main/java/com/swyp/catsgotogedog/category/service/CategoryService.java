package com.swyp.catsgotogedog.category.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swyp.catsgotogedog.category.domain.response.SubRegionResponse;
import com.swyp.catsgotogedog.category.domain.response.RegionHierarchyResponse;
import com.swyp.catsgotogedog.content.domain.entity.RegionCode;
import com.swyp.catsgotogedog.content.repository.RegionCodeRepository;
import com.swyp.catsgotogedog.global.exception.CatsgotogedogException;
import com.swyp.catsgotogedog.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryService {

	private final RegionCodeRepository regionCodeRepository;

	public Object findRegions(Integer sidoCode, Integer sigunguCode) {
		// sido, sigungu 함께 검색
		if(sidoCode != null && sigunguCode != null) {
			RegionCode sido = regionCodeRepository.findBySidoCodeAndRegionLevel(sidoCode, 1);
			if(sido != null) {
				RegionCode sigungu = regionCodeRepository.findByParentCodeAndSigunguCode(sido.getRegionId(), sigunguCode)
					.orElseThrow(() -> new CatsgotogedogException(ErrorCode.SIGUNGU_CODE_NOT_FOUND));

				return new SubRegionResponse(sigungu);
			}else
				throw new CatsgotogedogException(ErrorCode.SIDO_CODE_NOT_FOUND);
			// sidoCode만 입력
		} else if(sidoCode != null && sigunguCode == null) {
			RegionCode sido = regionCodeRepository.findBySidoCodeAndRegionLevel(sidoCode, 1);

			List<RegionCode> sigunguList = regionCodeRepository.findByParentCode(sido.getRegionId());

			return new RegionHierarchyResponse(sido, sigunguList);
			
			// 모든 지역코드
		} else if(sidoCode == null && sigunguCode == null) {
			List<RegionCode> allRegions = regionCodeRepository.findAll();

			Map<Integer, List<RegionCode>> childrenMap = allRegions.stream()
				.filter(region -> region.getRegionLevel() == 2)
				.collect(Collectors.groupingBy(RegionCode::getParentCode));

			return allRegions.stream()
				.filter(region -> region.getRegionLevel() == 1)
				.map(sido -> new RegionHierarchyResponse(
					sido,
					childrenMap.getOrDefault(sido.getSidoCode(), List.of())
				)).toList();
		} else {
			throw new CatsgotogedogException(ErrorCode.SIGUNGU_NEEDS_WITH_SIDO_CODE);
		}
	}
}
