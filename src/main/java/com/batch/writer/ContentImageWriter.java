package com.batch.writer;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.swyp.catsgotogedog.content.domain.entity.ContentImage;
import com.swyp.catsgotogedog.content.repository.ContentImageRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class ContentImageWriter implements ItemWriter<List<ContentImage>> {

	private final ContentImageRepository contentImageRepository;

	@Override
	@Transactional
	public void write(Chunk<? extends List<ContentImage>> chunk) throws Exception {
		List<ContentImage> flatList = chunk.getItems().stream()
			.flatMap(List::stream)
			.toList();
		log.info("{}개 이미지를 DB 삽입중", flatList.size());
		contentImageRepository.saveAll(flatList);
	}
}
