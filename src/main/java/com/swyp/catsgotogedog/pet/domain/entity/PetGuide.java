package com.swyp.catsgotogedog.pet.domain.entity;

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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "pet_guide")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PetGuide {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pet_guide_id")
	private Integer petGuideId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "content_id")
	private Content content;

	@Column(name = "accident_prep", length = 50)
	private String accidentPrep;

	@Column(name = "available_facility", length = 50)
	private String availableFacility;

	@Column(name = "provided_item", length = 50)
	private String providedItem;

	@Column(name = "etc_info")
	private String etcInfo;

	@Column(name = "purchasable_item", length = 50)
	private String purchasableItem;

	@Column(name = "allowed_pet_type", length = 255)
	private String allowedPetType;

	@Column(name = "rent_item", length = 50)
	private String rentItem;

	@Column(name = "pet_prep", length = 50)
	private String petPrep;

	@Column(name = "with_pet", length = 50)
	private String withPet;
}
