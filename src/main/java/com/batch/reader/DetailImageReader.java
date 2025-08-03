package com.batch.reader;

import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.content.repository.ContentImageRepository;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DetailImageReader {

	private final ContentImageRepository contentImageRepository;
	private final EntityManagerFactory entityManagerFactory;

	@Bean
	public JpaPagingItemReader<Content> detailImageContentReader() {
		return new JpaPagingItemReaderBuilder<Content>()
			.name("contentReader")
			.entityManagerFactory(entityManagerFactory)
			.pageSize(100)
			.queryString("SELECT c FROM Content c WHERE NOT EXISTS (SELECT 1 FROM ContentImage ci WHERE ci.content.contentId = c.contentId) ORDER BY c.contentId ASC")
			.build();
	}
}
