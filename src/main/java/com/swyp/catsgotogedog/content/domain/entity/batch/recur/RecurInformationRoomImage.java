package com.swyp.catsgotogedog.content.domain.entity.batch.recur;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recur_information_room_image")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecurInformationRoomImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer recurRoomImageId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recur_room_id")
	private RecurInformationRoom room;

	private String imageUrl;
	private String imageFilename;
	private String imageAlt;

	@Column(length = 50)
	private String imageCopyright;
}
