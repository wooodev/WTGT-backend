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
public class DetailInfoReader {

	private final EntityManagerFactory entityManagerFactory;

	@Bean
	public JpaPagingItemReader<Content> detailInfoItemReader() {
		String jpqlQuery = "SELECT c FROM Content c " +
			"WHERE NOT EXISTS (SELECT 1 FROM RecurInformation ri WHERE ri.content = c) " +
			"AND NOT EXISTS (SELECT 1 FROM RecurInformationRoom rir WHERE rir.content = c) " +
			"ORDER BY c.contentId ASC";

		return new JpaPagingItemReaderBuilder<Content>()
			.name("detailInfoItemReader")
			.entityManagerFactory(entityManagerFactory)
			.pageSize(100)
			.queryString(jpqlQuery)
			.build();
	}
}
