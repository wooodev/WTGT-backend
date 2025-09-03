package com.swyp.catsgotogedog.content.repository;

import com.swyp.catsgotogedog.content.repository.projection.RestDateProjection;
import org.springframework.data.jpa.repository.JpaRepository;

import com.swyp.catsgotogedog.content.domain.entity.batch.information.RestaurantInformation;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RestaurantInformationRepository extends JpaRepository<RestaurantInformation, Integer> {
    @Query("select r.restDate from RestaurantInformation r where r.content.contentId = :contentId")
    String findRestDateByContentId(int contentId);

    @Query("""
           SELECT r.content.contentId AS contentId,
                  r.restDate          AS restDate
           FROM RestaurantInformation r
           WHERE r.content.contentId IN :contentIds
           """)
    List<RestDateProjection> findRestDateByContentIdIn(List<Integer> contentIds);

    RestaurantInformation findByContent_ContentId(int contentId);
}

