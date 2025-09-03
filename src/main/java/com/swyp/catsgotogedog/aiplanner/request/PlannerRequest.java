package com.swyp.catsgotogedog.aiplanner.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swyp.catsgotogedog.aiplanner.domain.TravelDuration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Valid
public class PlannerRequest {

	@JsonProperty
	@NotNull(message = "여행 기간은 필수입니다.")
	private TravelDuration duration;

	@JsonProperty
	@NotNull(message = "대분류 여행 지역은 필수입니다.")
	@Min(value = 1, message = "올바른 시도 코드를 입력해주세요.")
	private Integer sidoCode;

	@JsonProperty
	@NotBlank(message = "현재 기분은 필수입니다.")
	@Size(max = 50, message = "기분 설명은 50자 이하로 입력해주세요")
	private String mood;

}
