package com.swyp.catsgotogedog.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.swyp.catsgotogedog.review.domain.entity.Review;
import com.swyp.catsgotogedog.review.domain.entity.ReviewImage;

public interface ReviewImageRepository extends JpaRepository<ReviewImage, Integer> {
	List<ReviewImage> findByReview(Review review);
}
