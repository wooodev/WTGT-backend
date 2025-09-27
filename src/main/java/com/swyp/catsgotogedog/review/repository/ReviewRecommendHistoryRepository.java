package com.swyp.catsgotogedog.review.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.swyp.catsgotogedog.review.domain.entity.Review;
import com.swyp.catsgotogedog.review.domain.entity.ReviewRecommendHistory;

public interface ReviewRecommendHistoryRepository extends JpaRepository<ReviewRecommendHistory, Integer> {

	@Query("SELECT r.review.reviewId FROM ReviewRecommendHistory r "
		+ "WHERE r.userId = :userId "
		+ "AND r.review IN :reviews")
	Set<Integer> findRecommendedReviewIdsByUserIdAndReviewIds(
		@Param("userId") int userId,
		@Param("reviews") List<Review> reviews);

	Optional<ReviewRecommendHistory> findReviewRecommendHistoryByReviewAndUserId(Review review, int userId);
}
