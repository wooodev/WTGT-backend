package com.batch.writer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.batch.dto.DetailInfoProcessResult;
import com.swyp.catsgotogedog.content.domain.entity.batch.recur.RecurInformation;
import com.swyp.catsgotogedog.content.domain.entity.batch.recur.RecurInformationRoom;
import com.swyp.catsgotogedog.content.repository.RecurInformationRepository;
import com.swyp.catsgotogedog.content.repository.RecurInformationRoomRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DetailInfoWriter implements ItemWriter<DetailInfoProcessResult> {

	private final RecurInformationRepository recurInformationRepository;
	private final RecurInformationRoomRepository recurInformationRoomRepository;

	@Override
	public void write(Chunk<? extends DetailInfoProcessResult> chunk) throws Exception {
		List<RecurInformation> generalToSave = new ArrayList<>();
		List<RecurInformationRoom> roomsToSave = new ArrayList<>();
		for(DetailInfoProcessResult result : chunk.getItems()) {
			if(result.getGeneralInfoList() != null) generalToSave.addAll(result.getGeneralInfoList());
			if(result.getRoomInfoList() != null) roomsToSave.addAll(result.getRoomInfoList());
		}
		if(!generalToSave.isEmpty()) recurInformationRepository.saveAll(generalToSave);
		if(!roomsToSave.isEmpty()) recurInformationRoomRepository.saveAll(roomsToSave);
	}
}
