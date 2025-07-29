package com.batch.reader;

import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swyp.catsgotogedog.content.domain.entity.Content;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DetailImageReader {

	private final EntityManagerFactory entityManagerFactory;

	@Bean
	public JpaPagingItemReader<Content> detailImageContentReader() {
		return new JpaPagingItemReaderBuilder<Content>()
			.name("contentReader")
			.entityManagerFactory(entityManagerFactory)
			.pageSize(100)
			.queryString("SELECT c FROM Content c ORDER BY c.contentId ASC")
			.build();
	}
}
