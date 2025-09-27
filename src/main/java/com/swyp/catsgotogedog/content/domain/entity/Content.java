package com.swyp.catsgotogedog.content.domain.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "content")
@ToString
@Builder
public class Content {
	@Id
	@Column(name = "content_id", updatable = false)
	private int contentId;

	@Column(name = "content_type_id", nullable = false)
	private int contentTypeId;

	@Column(name = "category_id")
	private String categoryId;

	@Column(name = "sido_code")
	private int sidoCode;

	@Column(name = "sigungu_code")
	private int sigunguCode;

	@Column(name = "addr1")
	private String addr1;

	@Column(name = "addr2")
	private String addr2;

	@Column(name = "image")
	private String image;

	@Column(name = "thumb_image")
	private String thumbImage;

	@Column(name = "copyright")
	private String copyright;

	@Column(name = "mapx")
	private double mapx;

	@Column(name = "mapy")
	private double mapy;

	@Column(name = "mlevel")
	private int mLevel;

	@Column(name = "tel")
	private String tel;

	@Column(name = "title")
	private String title;

	@Column(name = "zipcode")
	private int zipCode;

	@Lob
	@Column(name = "overview")
	private String overview;

	@CreatedDate
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	private LocalDateTime modifiedAt;

}
