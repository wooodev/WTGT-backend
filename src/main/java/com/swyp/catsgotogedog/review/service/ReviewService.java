package com.swyp.catsgotogedog.review.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.swyp.catsgotogedog.User.domain.entity.User;
import com.swyp.catsgotogedog.User.repository.UserRepository;
import com.swyp.catsgotogedog.common.util.image.storage.ImageStorageService;
import com.swyp.catsgotogedog.common.util.image.storage.dto.ImageInfo;
import com.swyp.catsgotogedog.common.util.image.validator.ImageUploadType;
import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.content.repository.ContentRepository;
import com.swyp.catsgotogedog.global.exception.CatsgotogedogException;
import com.swyp.catsgotogedog.global.exception.ErrorCode;
import com.swyp.catsgotogedog.review.domain.entity.Review;
import com.swyp.catsgotogedog.review.domain.entity.ReviewImage;
import com.swyp.catsgotogedog.review.domain.entity.ReviewRecommendHistory;
import com.swyp.catsgotogedog.review.domain.request.CreateReviewRequest;
import com.swyp.catsgotogedog.review.domain.response.ContentReviewPageResponse;
import com.swyp.catsgotogedog.review.domain.response.MyReviewPageResponse;
import com.swyp.catsgotogedog.review.domain.response.MyReviewResponse;
import com.swyp.catsgotogedog.review.domain.response.ReviewImageResponse;
import com.swyp.catsgotogedog.review.domain.response.ReviewResponse;
import com.swyp.catsgotogedog.review.repository.ReviewImageRepository;
import com.swyp.catsgotogedog.review.repository.ReviewRecommendHistoryRepository;
import com.swyp.catsgotogedog.review.repository.ReviewRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final ReviewImageRepository reviewImageRepository;
	private final UserRepository userRepository;
	private final ContentRepository contentRepository;
	private final ImageStorageService imageStorageService;
	private final ReviewRecommendHistoryRepository reviewRecommendHistoryRepository;
	private final ReviewReportService reviewReportService;

	// 리뷰 작성
	@Transactional
	public void createReview(int contentId, String userId, CreateReviewRequest request, List<MultipartFile> images) {
		User user = validateUser(userId);
		Content content = validateContent(contentId);


		Review uploadedReview = reviewRepository.save(Review.builder()
			.userId(user.getUserId())
			.contentEntity(content)
			.score(request.getScore())
			.content(request.getContent())
			.build());

		if(images != null && !images.isEmpty()) {
			if(images.size() > ImageUploadType.REVIEW.getMaxFiles()) {
				throw new CatsgotogedogException(ErrorCode.REVIEW_IMAGE_LIMIT_EXCEEDED);
			}
			uploadAndSaveReviewImages(uploadedReview, images);
		}

	}

	// 리뷰 수정
	@Transactional
	public void updateReview(int reviewId, String userId, CreateReviewRequest request, List<MultipartFile> images) {
		User user = validateUser(userId);
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CatsgotogedogException(ErrorCode.REVIEW_NOT_FOUND));

		if(user.getUserId() != review.getUserId()) {
			throw new CatsgotogedogException(ErrorCode.REVIEW_FORBIDDEN_ACCESS);
		}

		review.setScore(request.getScore());
		review.setContent(request.getContent());

		if(images != null && !images.isEmpty()) {
			List<ReviewImage> reviewImages = reviewImageRepository.findByReview(review);
			int totalImages = reviewImages.size() + images.size();

			if(totalImages > ImageUploadType.REVIEW.getMaxFiles() || images.size() > ImageUploadType.REVIEW.getMaxFiles()) {
				throw new CatsgotogedogException(ErrorCode.REVIEW_IMAGE_LIMIT_EXCEEDED);
			}

			List<ImageInfo> imageInfos = imageStorageService.upload(images, ImageUploadType.REVIEW);
			List<ReviewImage> saveImages = imageInfos.stream()
				.map(imageInfo -> ReviewImage.builder()
					.review(review)
					.imageFilename(imageInfo.key())
					.imageUrl(imageInfo.url())
					.build()
				).toList();
			reviewImageRepository.saveAll(saveImages);
		}
	}

	// 리뷰 삭제
	@Transactional
	public void deleteReview(int reviewId, String userId) {
		User user = validateUser(userId);
		validateReview(reviewId);
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CatsgotogedogException(ErrorCode.REVIEW_NOT_FOUND));

		if(user.getUserId() != review.getUserId()) {
			throw new CatsgotogedogException(ErrorCode.REVIEW_FORBIDDEN_ACCESS);
		}

		List<ReviewImage> images = reviewImageRepository.findByReview(review);

		images.forEach(image -> imageStorageService.delete(image.getImageFilename()));

		reviewRepository.delete(review);

	}

	// 리뷰 이미지 삭제
	@Transactional
	public void deleteReviewImage(int reviewId, int imageId, String userId) {
		User user = validateUser(userId);
		validateReview(reviewId);

		Review review = reviewRepository.findById(reviewId)
				.orElseThrow(() -> new CatsgotogedogException(ErrorCode.REVIEW_NOT_FOUND));

		if(user.getUserId() != review.getUserId()) {
			throw new CatsgotogedogException(ErrorCode.REVIEW_FORBIDDEN_ACCESS);
		}

		ReviewImage image = reviewImageRepository.findById(imageId)
				.orElseThrow(() -> new CatsgotogedogException(ErrorCode.REVIEW_IMAGE_NOT_FOUND));

		imageStorageService.delete(image.getImageFilename());
		reviewImageRepository.deleteById(imageId);
	}

	// ContentId를 통한 리뷰 목록 조회
	@Transactional(readOnly = true)
	public ContentReviewPageResponse fetchReviewsByContentId(int contentId, String sort, Pageable pageable, String userId) {
		validateContent(contentId);

		Sort sortObj = createSort(sort);
		Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sortObj);

		// 페이징 리뷰
		Page<Review> reviewPage = reviewRepository.findByContentIdWithUserAndReviewImages(contentId, sortedPageable);
		List<Review> reviews = reviewRepository.findByContentEntityContentId((contentId));

		List<ReviewImageResponse> contentReviewImages = reviews.stream()
			.flatMap(review -> review.getReviewImages().stream())
				.map(ReviewImageResponse::from)
				.toList();

		Set<Integer> recommendedReviewIds;
		if(userId != null) {
			recommendedReviewIds = reviewRecommendHistoryRepository.findRecommendedReviewIdsByUserIdAndReviewIds(
					Integer.parseInt(userId),
					reviewPage.getContent()
			);
		} else {
			recommendedReviewIds = new HashSet<>();
		}

		List<Integer> userIds = reviewPage.getContent().stream()
			.map(Review::getUserId)
			.distinct()
			.collect(Collectors.toList());

		Map<Integer, User> userMap = userRepository.findAllById(userIds).stream()
			.collect(Collectors.toMap(User::getUserId, Function.identity()));

		List<ReviewResponse> reviewResponses = reviewPage.getContent().stream()
			.map(review -> {
				User user = userMap.get(review.getUserId());
				return new ReviewResponse(
					review.getContentEntity().getContentId(),
					review.getReviewId(),
					user != null ? user.getUserId() : 0,
					user != null ? user.getDisplayName() : "알 수 없음",
					user != null ? user.getImageUrl() : "",
					review.getContent(),
					review.getScore(),
					review.getCreatedAt(),
					review.getRecommendedNumber(),
					recommendedReviewIds.contains(review.getReviewId()),
					reviewReportService.isBlindReview(review.getReviewId()),
					review.getReviewImages().stream()
						.map(ReviewImageResponse::from)
						.collect(Collectors.toList())
				);
			}).toList();

		return new ContentReviewPageResponse(
			(int) reviewPage.getTotalElements(),
			reviewPage.getTotalPages(),
			reviewPage.getNumber(),
			reviewPage.getSize(),
			reviewPage.hasNext(),
			reviewPage.hasPrevious(),
			reviewResponses,
			contentReviewImages
		);
	}

	// 자신이 작성한 리뷰 목록 페이징
	@Transactional(readOnly = true)
	public MyReviewPageResponse fetchReviewsByUserId(String userId, Pageable pageable) {
		validateUser(userId);

		Page<Review> reviewPage = reviewRepository.findByUserId(Integer.parseInt(userId), pageable);

		List<MyReviewResponse> myReviewResponses = reviewPage.getContent().stream()
			.map(this::toReviewResponse)
			.toList();

		return new MyReviewPageResponse(
			(int) reviewPage.getTotalElements(),
			reviewPage.getTotalPages(),
			reviewPage.getNumber(),
			reviewPage.getSize(),
			reviewPage.hasNext(),
			reviewPage.hasPrevious(),
			myReviewResponses
		);
	}

	// 유저의 특정 리뷰 데이터 조회
	@Transactional(readOnly = true)
	public MyReviewResponse fetchReviewById(int reviewId, String userId) {
		validateUser(userId);
		validateReview(reviewId);

		Review review = reviewRepository.findByIdAndUserId(reviewId, userId)
			.orElseThrow(() -> new CatsgotogedogException(ErrorCode.REVIEW_FORBIDDEN_ACCESS));

		return toReviewResponse(review);
	}


	private User validateUser(String userId) {
		return userRepository.findById(Integer.parseInt(userId))
			.orElseThrow(() -> new CatsgotogedogException(ErrorCode.MEMBER_NOT_FOUND));
	}

	private Content validateContent(int contentId) {
		return contentRepository.findById(contentId)
			.orElseThrow(() -> new CatsgotogedogException(ErrorCode.CONTENT_NOT_FOUND));
	}

	private Review validateReview(int reviewId) {
		return reviewRepository.findById(reviewId)
			.orElseThrow(() -> new CatsgotogedogException(ErrorCode.REVIEW_NOT_FOUND));
	}

	private void uploadAndSaveReviewImages(Review review, List<MultipartFile> images) {
		if(images.size() > ImageUploadType.REVIEW.getMaxFiles()) {
			throw new CatsgotogedogException(ErrorCode.REVIEW_IMAGE_LIMIT_EXCEEDED);
		}

		List<ImageInfo> imageInfos = imageStorageService.upload(images, ImageUploadType.REVIEW);

		List<ReviewImage> saveImages = imageInfos.stream()
			.map(imageInfo -> ReviewImage.builder()
				.review(review)
				.imageFilename(imageInfo.key())
				.imageUrl(imageInfo.url())
				.build()
			).toList();

		reviewImageRepository.saveAll(saveImages);
	}

	private MyReviewResponse toReviewResponse(Review review) {
		return new MyReviewResponse(
			review.getContentEntity().getContentId(),
			review.getContentEntity().getTitle(),
			review.getReviewId(),
			review.getContent(),
			review.getScore(),
			review.getRecommendedNumber(),
			review.getCreatedAt(),
			review.getReviewImages().stream()
				.map(ReviewImageResponse::from)
				.toList()
		);
	}

	private Sort createSort(String sort) {
		return switch(sort) {
			case "r" -> Sort.by(Sort.Direction.DESC, "recommendedNumber")
				.and(Sort.by(Sort.Direction.DESC, "createdAt"));
			case "c" -> Sort.by(Sort.Direction.DESC, "createdAt");
			default -> Sort.by(Sort.Direction.DESC, "recommendedNumber")
				.and(Sort.by(Sort.Direction.DESC, "createdAt"));
		};
	}

}
