package com.swyp.catsgotogedog.content.domain.entity.batch.information;

import java.time.LocalTime;


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
@Table(name = "lodge_information")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LodgeInformation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lodge_id")
	private Integer lodgeId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "content_id")
	private Content content;

	@Column(name = "capacity_count")
	private Integer capacityCount;

	private Boolean goodstay;

	private Boolean benikia;

	@Column(name = "check_in_time")
	private String checkInTime;

	@Column(name = "check_out_time")
	private String checkOutTime;

	@Column(name = "cooking", length = 50)
	private String cooking;

	@Column(name = "foodplace", length = 50)
	private String foodplace;

	private Boolean hanok;

	@Column(name = "information", length = 50)
	private String information;

	@Column(name = "parking", length = 50)
	private String parking;

	@Column(name = "pickup_service")
	private String pickupService;

	@Column(name = "room_count")
	private Integer roomCount;

	@Column(name = "reservation_info", length = 30)
	private String reservationInfo;

	@Column(name = "reservation_url", length = 50)
	private String reservationUrl;

	@Column(name = "room_type", length = 30)
	private String roomType;

	@Column(name = "scale", length = 30)
	private String scale;

	@Column(name = "sub_facility", length = 50)
	private String subFacility;

	private Boolean barbecue;
	private Boolean beauty;
	private Boolean beverage;
	private Boolean bicycle;
	private Boolean campfire;
	private Boolean fitness;
	private Boolean karaoke;
	@Column(name = "public_bath")
	private Boolean publicBath;
	@Column(name = "public_pc_room")
	private Boolean publicPcRoom;
	private Boolean sauna;
	private Boolean seminar;
	private Boolean sports;

	@Column(name = "refund_regulation", length = 100)
	private String refundRegulation;
}
