package com.swyp.catsgotogedog.content.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.swyp.catsgotogedog.content.domain.entity.RegionCode;

public interface RegionCodeRepository extends JpaRepository<RegionCode, Integer> {
  RegionCode findBySidoCodeAndRegionLevel(int sidoCode, int regionLevel);

  RegionCode findByParentCodeAndSigunguCodeAndRegionLevel(int parentCode, int sigunguCode, int regionLevel);

	List<RegionCode> findByRegionLevel(int regionLevel);

	Optional<RegionCode> findByParentCodeAndSigunguCode(int regionId, Integer sigunguCode);

	List<RegionCode> findByParentCode(int regionId);
}
