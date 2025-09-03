package com.swyp.catsgotogedog.aiplanner.domain;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Document(indexName = "content-embedding")
@Setting(settingPath = "elasticsearch/content-embed-setting.json")
@Mapping(mappingPath = "elasticsearch/content-embed-mapping.json")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class ContentEmbedding {

	@Id
	private Integer contentId;
	private int contentTypeId;
	private String categoryId;
	private int sidoCode;
	private int sigunguCode;
	private double mapx;
	private double mapy;
	private String title;

	@Field(type = FieldType.Dense_Vector, dims = 1024)
	private List<Float> embedding;
}
