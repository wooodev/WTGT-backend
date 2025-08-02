package com.swyp.catsgotogedog.content.domain.entity.batch.recur;

import com.swyp.catsgotogedog.content.domain.entity.Content;

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
@Table(name = "recur_information")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecurInformation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int recurId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "content_id")
	private Content content;

	private String infoName;

	@Column(columnDefinition = "TEXT")
	private String infoText;
}
