package com.swyp.catsgotogedog.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swyp.catsgotogedog.content.domain.entity.batch.information.LodgeInformation;

public interface LodgeInformationRepository extends JpaRepository<LodgeInformation, Integer> {
    LodgeInformation findByContent_ContentId(int contentId);
}
