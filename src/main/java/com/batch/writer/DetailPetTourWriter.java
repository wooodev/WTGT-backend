package com.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.swyp.catsgotogedog.pet.domain.entity.PetGuide;
import com.swyp.catsgotogedog.pet.repository.PetGuideRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DetailPetTourWriter implements ItemWriter<PetGuide> {

	private final PetGuideRepository petGuideRepository;

	@Override
	public void write(Chunk<? extends PetGuide> chunk) throws Exception {
		petGuideRepository.saveAll(chunk.getItems());
	}
}
