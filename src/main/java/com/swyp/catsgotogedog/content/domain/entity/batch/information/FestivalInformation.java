package com.swyp.catsgotogedog.content.domain.entity.batch.information;

import java.time.LocalDate;

import com.swyp.catsgotogedog.content.domain.entity.Content;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "festival_information")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FestivalInformation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "festival_id")
	private Integer festivalId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "content_id")
	private Content content;

	@Column(name = "age_limit")
	private String ageLimit;

	@Column(name = "booking_place")
	private String bookingPlace;

	@Column(name = "discount_info")
	private String discountInfo;

	@Column(name = "event_start_date")
	private LocalDate eventStartDate;

	@Column(name = "event_end_date")
	private LocalDate eventEndDate;

	@Column(name = "event_homepage")
	private String eventHomepage;

	@Column(name = "event_place")
	private String eventPlace;

	@Column(name = "place_info")
	private String placeInfo;

	@Column(name = "play_time")
	private String playTime;

	@Column(name = "program")
	private String program;

	@Column(name = "spend_time")
	private String spendTime;

	@Column(name = "organizer")
	private String organizer;

	@Column(name = "organizer_tel")
	private String organizerTel;

	@Column(name = "supervisor")
	private String supervisor;

	@Column(name = "supervisor_tel")
	private String supervisorTel;

	@Column(name = "sub_event")
	private String subEvent;

	@Column(name = "fee_info")
	private String feeInfo;
}