package com.swyp.catsgotogedog.content.domain.entity.batch.information;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

import com.swyp.catsgotogedog.content.domain.entity.Content;

@Entity
@Table(name = "sights_information")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SightsInformation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sights_id")
	private Integer sightsId;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "content_id")
	private Content content;

	@Column(name = "content_type_id")
	private Integer contentTypeId;

	@Column(name = "accom_count")
	private Integer accomCount;

	@Column(name = "chk_creditcard")
	private String chkCreditcard;

	@Column(name = "exp_age_range")
	private String expAgeRange;

	@Column(name = "exp_guide")
	private String expGuide;

	@Column(name = "info_center")
	private String infoCenter;

	@Column(name = "open_date")
	private LocalDate openDate;

	@Column(name = "parking")
	private String parking;

	@Column(name = "rest_date")
	private String restDate;

	@Column(name = "use_season")
	private String useSeason;

	@Column(name = "use_time")
	private String useTime;

	private Boolean heritage1;
	private Boolean heritage2;
	private Boolean heritage3;
}