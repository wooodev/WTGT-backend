package com.swyp.catsgotogedog.content.repository;

import com.swyp.catsgotogedog.content.repository.projection.RestDateProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import com.swyp.catsgotogedog.content.domain.entity.batch.information.SightsInformation;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SightsInformationRepository extends JpaRepository<SightsInformation, Integer> {
    @Query("select s.restDate from SightsInformation s where s.content.contentId = :contentId")
    String findRestDateByContentId(int contentId);
    @Query("""
           SELECT s.content.contentId AS contentId,
                  s.restDate          AS restDate
           FROM SightsInformation s
           WHERE s.content.contentId IN :contentIds
           """)
    List<RestDateProjection> findRestDateByContentIdIn(List<Integer> contentIds);

    SightsInformation findByContent_ContentId(int contentId);
}
