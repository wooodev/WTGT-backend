package com.swyp.catsgotogedog.content.domain.entity.batch.recur;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.swyp.catsgotogedog.content.domain.entity.Content;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recur_information_room")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecurInformationRoom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer recurRoomId;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "content_id")
	private Content content;

	private String roomTitle;
	private Integer roomSize1;
	private Integer roomCount;
	private Integer roomBaseCount;
	private Integer roomMaxCount;
	private Integer offSeasonWeekMinFee;
	private Integer offSeasonWeekendMinFee;
	private Integer peakSeasonWeekMinFee;
	private Integer peakSeasonWeekendMinFee;

	@Column(columnDefinition = "TEXT")
	private String roomIntro;

	@Column(name = "room_bath_facility")
	private Boolean roomBathFacility; // 오타 수정: facility
	private Boolean roomBath;
	private Boolean roomHomeTheater;
	private Boolean roomAircondition;
	private Boolean roomTv;
	private Boolean roomPc;
	private Boolean roomCable;
	private Boolean roomInternet;
	private Boolean roomRefrigerator;
	private Boolean roomToiletries;
	private Boolean roomSofa;
	private Boolean roomCook;
	private Boolean roomTable;
	private Boolean roomHairdryer;

	@Column(precision = 10, scale = 2)
	private BigDecimal roomSize2;

	@OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<RecurInformationRoomImage> images;

}
