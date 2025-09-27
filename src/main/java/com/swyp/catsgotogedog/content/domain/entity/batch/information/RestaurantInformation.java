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
@Table(name = "restaurant_information")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantInformation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "restaurant_id")
	private Integer restaurantId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "content_id")
	private Content content;

	@Column(name = "chk_creditcard")
	private String chkCreditcard;

	@Column(name = "discount_info")
	private String discountInfo;

	@Column(name = "signature_menu")
	private String signatureMenu;

	@Column(name = "information")
	private String information;

	@Column(name = "kids_facility")
	private Boolean kidsFacility;

	@Column(name = "open_date")
	private LocalDate openDate;

	@Column(name = "open_time")
	private String openTime;

	@Column(name = "takeout")
	private String takeout;

	@Column(name = "parking")
	private String parking;

	@Column(name = "reservation")
	private String reservation;

	@Column(name = "rest_date")
	private String restDate;

	private Integer scale;
	private String seat;
	private Boolean smoking;

	@Column(name = "treat_menu")
	private String treatMenu;
}
