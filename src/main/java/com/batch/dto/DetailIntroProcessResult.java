package com.batch.dto;

import java.util.List;

import com.swyp.catsgotogedog.content.domain.entity.batch.information.FestivalInformation;
import com.swyp.catsgotogedog.content.domain.entity.batch.information.LodgeInformation;
import com.swyp.catsgotogedog.content.domain.entity.batch.information.RestaurantInformation;
import com.swyp.catsgotogedog.content.domain.entity.batch.information.SightsInformation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class DetailIntroProcessResult {
	private final List<SightsInformation> sightsInfoList;
	private final List<LodgeInformation> lodgeInfoList;
	private final List<RestaurantInformation> restaurantInfoList;
	private final List<FestivalInformation> festivalInformationList;

}
