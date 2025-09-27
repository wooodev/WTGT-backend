package com.swyp.catsgotogedog.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swyp.catsgotogedog.User.domain.entity.User;
import com.swyp.catsgotogedog.review.domain.entity.Review;
import com.swyp.catsgotogedog.review.domain.entity.ReviewReport;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Integer> {
	Optional<ReviewReport> findByUserAndReview(User reporter, Review targetReview);

	List<ReviewReport> findByReview(Review review);
}
