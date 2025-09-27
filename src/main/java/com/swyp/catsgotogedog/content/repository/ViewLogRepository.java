package com.swyp.catsgotogedog.content.repository;

import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.content.domain.entity.ViewLog;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ViewLogRepository extends JpaRepository<ViewLog, Integer> {
    @Query("""
      SELECT v.content
        FROM ViewLog v
       WHERE v.user.userId = :userId
    GROUP BY v.content
    ORDER BY MAX(v.viewedAt) DESC
    """)
    List<Content> findRecentContentByUser(int userId, Pageable pageable);

	@Query("""
		SELECT v.content.contentId 
		FROM ViewLog v 
		WHERE v.viewedAt >= :startDate
		GROUP BY v.content.contentId
		ORDER BY COUNT(v.content.contentTypeId) DESC
	""")
	List<Integer> findTopContentViews(
		@Param("startDate") LocalDateTime startDate,
		Pageable pageable
	);
}
