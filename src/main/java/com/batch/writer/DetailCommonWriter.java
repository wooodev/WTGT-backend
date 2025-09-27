package com.batch.writer;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.content.repository.ContentRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DetailCommonWriter implements ItemWriter<Content> {


	@Override
	public void write(Chunk<? extends Content> chunk) throws Exception {
	}
}
