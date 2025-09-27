package com.swyp.catsgotogedog.review.repository;

import com.swyp.catsgotogedog.review.domain.entity.ContentReview;
import com.swyp.catsgotogedog.review.repository.projection.AvgScoreProjection;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ContentReviewRepository extends JpaRepository<ContentReview, Integer> {
    ContentReview findByContentId(int contentId);

    @Query("select avg(cr.score) from ContentReview cr where cr.contentId = :contentId")
    Double findAvgScoreByContentId(@Param("contentId") int contentId);

    @Query("""
           SELECT cr.contentId AS contentId,
                  AVG(cr.score) AS avgScore
           FROM ContentReview cr
           WHERE cr.contentId IN :contentIds
           GROUP BY cr.contentId
           """)
    List<AvgScoreProjection> findAvgScoreByContentIdIn(@Param("contentIds") List<Integer> contentIds);
}
