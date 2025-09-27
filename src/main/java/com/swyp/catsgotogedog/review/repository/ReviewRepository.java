package com.swyp.catsgotogedog.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.review.domain.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
	/**
	 * reviewId와 userId 를 통한 리뷰 컬렉션 조회
	 * @param reviewId
	 * @param userId
	 * @return Optional<Review>
	 */
	@Query("SELECT r FROM Review r WHERE r.reviewId = :reviewId AND r.userId = :userId")
	Optional<Review> findByIdAndUserId(@Param("reviewId") int reviewId, @Param("userId") String userId);

	/**
	 * contentId와 pageable 요소를 통한 페이징 Review 목록
	 * @param contentId
	 * @param pageable
	 * @return 페이지네이션 Review
	 */
	@Query("SELECT DISTINCT r FROM Review r "
		+ "LEFT JOIN FETCH r.reviewImages "
		+ "WHERE r.contentEntity.contentId = :contentId")
	Page<Review> findByContentIdWithUserAndReviewImages(
		@Param("contentId") int contentId,
		Pageable pageable);

	/**
	 * userId와 pageable 요소를 통한 자신이 작성한 페이징 Review 목록
	 * @param userId
	 * @param pageable
	 * @return 페이지네이션 Review
	 */
	@EntityGraph(attributePaths = {"reviewImages"})
	Page<Review> findByUserId(int userId, Pageable pageable);

	List<Review> findByContentEntity(Content contentEntity);

	List<Review> findByContentEntityContentId(int contentEntityContentId);
}
