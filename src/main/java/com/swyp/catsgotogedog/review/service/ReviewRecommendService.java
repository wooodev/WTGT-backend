package com.swyp.catsgotogedog.review.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swyp.catsgotogedog.User.domain.entity.User;
import com.swyp.catsgotogedog.User.repository.UserRepository;
import com.swyp.catsgotogedog.global.exception.CatsgotogedogException;
import com.swyp.catsgotogedog.global.exception.ErrorCode;
import com.swyp.catsgotogedog.review.domain.entity.Review;
import com.swyp.catsgotogedog.review.domain.entity.ReviewRecommendHistory;
import com.swyp.catsgotogedog.review.repository.ReviewRecommendHistoryRepository;
import com.swyp.catsgotogedog.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewRecommendService {

	private final UserRepository userRepository;
	private final ReviewRepository reviewRepository;
	private final ReviewRecommendHistoryRepository reviewRecommendHistoryRepository;

	// 좋아요 처리
	@Transactional
	public void recommendReview(int reviewId, String strUserId) {
		int userId = Integer.parseInt(strUserId);
		User user = validateUser(userId);
		Review targetReview = validateReview(reviewId);

		reviewRecommendHistoryRepository.findReviewRecommendHistoryByReviewAndUserId(targetReview, userId)
			.ifPresent(reviewRecommendHistory -> {
					throw new CatsgotogedogException(ErrorCode.ALREADY_RECOMMENDED);
				});
		reviewRecommendHistoryRepository.save(ReviewRecommendHistory.builder()
			.userId(user.getUserId())
			.review(targetReview)
			.build());

		targetReview.setRecommendedNumber(targetReview.getRecommendedNumber() + 1);
	}

	// 좋아요 해제 처리
	@Transactional
	public void cancelRecommendReview(int reviewId, String strUserId) {
		int userId = Integer.parseInt(strUserId);
		User user = validateUser(userId);
		Review targetReview = validateReview(reviewId);

		reviewRecommendHistoryRepository.findReviewRecommendHistoryByReviewAndUserId(targetReview, userId)
			.ifPresentOrElse(reviewRecommendHistoryRepository::delete,
				() -> {
					throw new CatsgotogedogException(ErrorCode.NOT_RECOMMENDED_REVIEW);
				}
			);
		targetReview.setRecommendedNumber(targetReview.getRecommendedNumber() - 1);
	}

	private User validateUser(int userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new CatsgotogedogException(ErrorCode.MEMBER_NOT_FOUND));
	}

	private Review validateReview(int reviewId) {
		return reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CatsgotogedogException(ErrorCode.REVIEW_NOT_FOUND));
	}
}
