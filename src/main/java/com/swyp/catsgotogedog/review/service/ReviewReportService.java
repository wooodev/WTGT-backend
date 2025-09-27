package com.swyp.catsgotogedog.review.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.swyp.catsgotogedog.User.domain.entity.User;
import com.swyp.catsgotogedog.User.repository.UserRepository;
import com.swyp.catsgotogedog.global.exception.CatsgotogedogException;
import com.swyp.catsgotogedog.global.exception.ErrorCode;
import com.swyp.catsgotogedog.review.domain.entity.ReportReason;
import com.swyp.catsgotogedog.review.domain.entity.Review;
import com.swyp.catsgotogedog.review.domain.entity.ReviewReport;
import com.swyp.catsgotogedog.review.repository.ReportReasonRepository;
import com.swyp.catsgotogedog.review.repository.ReviewReportRepository;
import com.swyp.catsgotogedog.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewReportService {

	private final int REPORT_BLIND_COUNTS = 5;

	private final UserRepository userRepository;
	private final ReportReasonRepository reportReasonRepository;
	private final ReviewReportRepository reviewReportRepository;
	private final ReviewRepository reviewRepository;

	// 리뷰 신고
	public void reportReview(Integer reviewId, Integer reviewReportId, String stringUserId) {
		User reporter = validateUser(stringUserId);

		ReportReason reason = reportReasonRepository.findById(reviewReportId)
			.orElseThrow(() -> new CatsgotogedogException(ErrorCode.REPORT_REASON_NOT_FOUND));

		Review targetReview = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CatsgotogedogException(ErrorCode.REVIEW_NOT_FOUND));

		if(reporter.getUserId() ==  targetReview.getUserId()) {
			throw new CatsgotogedogException(ErrorCode.OWN_REVIEW_CANT_REPORT);
		}

		reviewReportRepository.findByUserAndReview(reporter, targetReview)
			.ifPresentOrElse(
				// 신고 내역이 존재하여 신고 처리 안됨.
				existReport -> {
					throw new CatsgotogedogException(ErrorCode.ALREADY_REPORTED);
				},
				// 신고 내역이 없을 경우 신고 처리
				() -> reviewReportRepository.save(
				ReviewReport.builder()
				.user(reporter)
				.reportReason(reason)
				.review(targetReview)
				.build()));
	}

	// 특정 리뷰 블라인드 여부
	public boolean isBlindReview(Integer reviewId) {
		Review targetReview = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CatsgotogedogException(ErrorCode.REVIEW_NOT_FOUND));

		List<ReviewReport> reports = reviewReportRepository.findByReview(targetReview);

		Map<ReportReason, Long> reportCountByReason = reports.stream()
			.collect(Collectors.groupingBy(
				ReviewReport::getReportReason,
				Collectors.counting()
			));

		return reportCountByReason.values().stream()
			.anyMatch(count -> count >= REPORT_BLIND_COUNTS);
	}


	public List<ReportReason> fetchReasons() {
		return reportReasonRepository.findAll();
	}

	private User validateUser(String userId) {
		return userRepository.findById(Integer.parseInt(userId))
			.orElseThrow(() -> new CatsgotogedogException(ErrorCode.MEMBER_NOT_FOUND));
	}
}
