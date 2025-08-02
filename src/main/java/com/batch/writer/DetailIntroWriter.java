package com.batch.writer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.batch.dto.DetailIntroProcessResult;
import com.swyp.catsgotogedog.content.domain.entity.batch.information.FestivalInformation;
import com.swyp.catsgotogedog.content.domain.entity.batch.information.LodgeInformation;
import com.swyp.catsgotogedog.content.domain.entity.batch.information.RestaurantInformation;
import com.swyp.catsgotogedog.content.domain.entity.batch.information.SightsInformation;
import com.swyp.catsgotogedog.content.repository.FestivalInformationRepository;
import com.swyp.catsgotogedog.content.repository.LodgeInformationRepository;
import com.swyp.catsgotogedog.content.repository.RestaurantInformationRepository;
import com.swyp.catsgotogedog.content.repository.SightsInformationRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DetailIntroWriter implements ItemWriter<DetailIntroProcessResult> {

	private final SightsInformationRepository sightsInformationRepository;
	private final LodgeInformationRepository lodgeInformationRepository;
	private final RestaurantInformationRepository restaurantInformationRepository;
	private final FestivalInformationRepository festivalInformationRepository;

	@Override
	public void write(Chunk<? extends DetailIntroProcessResult> chunk) throws Exception {
		List<SightsInformation> sightsToSave = new ArrayList<>();
		List<LodgeInformation> lodgeToSave = new ArrayList<>();
		List<RestaurantInformation> restaurantToSave = new ArrayList<>();
		List<FestivalInformation> festivalToSave = new ArrayList<>();

		for(DetailIntroProcessResult result : chunk.getItems()) {
			if(result.getSightsInfoList() != null) sightsToSave.addAll(result.getSightsInfoList());
			if(result.getLodgeInfoList() != null) lodgeToSave.addAll(result.getLodgeInfoList());
			if(result.getRestaurantInfoList() != null) restaurantToSave.addAll(result.getRestaurantInfoList());
			if(result.getFestivalInformationList() != null) festivalToSave.addAll(result.getFestivalInformationList());
		}

		if(!sightsToSave.isEmpty()) sightsInformationRepository.saveAll(sightsToSave);
		if(!lodgeToSave.isEmpty()) lodgeInformationRepository.saveAll(lodgeToSave);
		if(!restaurantToSave.isEmpty()) restaurantInformationRepository.saveAll(restaurantToSave);
		if(!festivalToSave.isEmpty()) festivalInformationRepository.saveAll(festivalToSave);

	}
}
