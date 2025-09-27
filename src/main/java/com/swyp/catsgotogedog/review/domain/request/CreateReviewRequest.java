package com.swyp.catsgotogedog.review.domain.request;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReviewRequest {

	@NotNull(message = "별점을 입력해주세요")
	@DecimalMin(value = "0.5", message = "별점은 0.5 이상이어야 합니다.")
	@DecimalMax(value = "5.0", message = "별점은 5.0 이하이어야 합니다.")
	@Schema(example = "3.5", description = "0.5 ~ 5.0 (.5 단위) 입력")
	private BigDecimal score;
	@NotEmpty(message = "리뷰 내용을 입력해주세요.")
	@Schema(example = "리뷰 내용", description = "리뷰 내용 입력")
	private String content;
}
