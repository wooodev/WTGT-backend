package com.swyp.catsgotogedog.category.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "category_code")
@Builder
public class CategoryCode {

	@Id
	@Column(name = "category_id", length = 30)
	private String categoryId;

	@Column(name = "category_name", length = 50, nullable = false)
	private String categoryName;

	@Column(name = "content_type_id", nullable = false)
	private int contentTypeId;
}
