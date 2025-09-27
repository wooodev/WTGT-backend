package com.swyp.catsgotogedog.review.domain.entity;

import java.math.BigDecimal;
import java.util.List;

import com.swyp.catsgotogedog.User.domain.entity.User;
import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.global.BaseTimeEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@Table(name = "content_review")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Review extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int reviewId;
	private int userId;
	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "contentId")
	private Content contentEntity;

	@Column(precision = 2, scale = 1)
	@DecimalMin(value = "0.5", message = "별점은 0.5 이상이어야 합니다.")
	@DecimalMax(value = "5.0", message = "별점은 5.0 이하이어야 합니다.")
	private BigDecimal score;

	private int recommendedNumber;

	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ReviewImage> reviewImages;
}
