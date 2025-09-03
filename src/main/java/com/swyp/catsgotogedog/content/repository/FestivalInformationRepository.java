package com.swyp.catsgotogedog.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swyp.catsgotogedog.content.domain.entity.batch.information.FestivalInformation;

public interface FestivalInformationRepository extends JpaRepository<FestivalInformation, Integer> {
    FestivalInformation findByContent_ContentId(int contentId);
}
