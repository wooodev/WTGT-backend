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
public class DetailCommonReader {

	private final EntityManagerFactory entityManagerFactory;

	@Bean
	public JpaPagingItemReader<Content> detailCommonItemReader() {
		String jpqlQuery = "SELECT c FROM Content c WHERE c.overview IS NULL OR c.overview = '' ORDER BY c.contentId ASC";

		return new JpaPagingItemReaderBuilder<Content>()
			.name("detailCommonItemReader")
			.entityManagerFactory(entityManagerFactory)
			.pageSize(100)
			.queryString(jpqlQuery)
			.build();
	}
}
