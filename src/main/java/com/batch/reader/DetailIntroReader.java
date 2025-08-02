package com.batch.reader;

import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swyp.catsgotogedog.content.domain.entity.Content;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DetailIntroReader {
	private final EntityManagerFactory entityManagerFactory;

	@Bean
	public JpaCursorItemReader<Content> sightsInformationItemReader() {
		String jpqlQuery =
			"SELECT c "
			+ "FROM Content c "
			+ "WHERE NOT EXISTS (SELECT 1 FROM SightsInformation si WHERE si.content = c)";
		return new JpaCursorItemReaderBuilder<Content>()
			.name("sightsInformationItemReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString(jpqlQuery)
			.build();
	}

	@Bean
	public JpaCursorItemReader<Content> lodgeInformationItemReader() {
		String jpqlQuery =
			"SELECT c "
			+ "FROM Content c "
			+ "WHERE NOT EXISTS (SELECT 1 FROM LodgeInformation li WHERE li.content = c)";
		return new JpaCursorItemReaderBuilder<Content>()
			.name("lodgeInformationItemReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString(jpqlQuery)
			.build();
	}

	@Bean
	public JpaCursorItemReader<Content> festivalInformationItemReader() {
		String jpqlQuery =
			"SELECT c "
			+ "FROM Content c "
			+ "WHERE NOT EXISTS (SELECT 1 FROM FestivalInformation fi WHERE fi.content = c)";
		return new JpaCursorItemReaderBuilder<Content>()
			.name("festivalInformationItemReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString(jpqlQuery)
			.build();
	}

	//
	@Bean
	public JpaCursorItemReader<Content> restaurantInformationItemReader() {
		String jpqlQuery =
			"SELECT c "
			+ "FROM Content c "
			+ "WHERE NOT EXISTS (SELECT 1 FROM RestaurantInformation ri WHERE ri.content = c)";
		return new JpaCursorItemReaderBuilder<Content>()
			.name("restaurantInformationItemReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString(jpqlQuery)
			.build();
	}
}
