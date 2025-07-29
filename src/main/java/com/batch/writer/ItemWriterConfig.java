package com.batch.writer;

import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swyp.catsgotogedog.content.domain.entity.Content;

import jakarta.persistence.EntityManagerFactory;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class ItemWriterConfig {

	@Bean
	public JpaItemWriter<Content> step1ContentWriter(EntityManagerFactory entityManagerFactory) {
		JpaItemWriter<Content> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(entityManagerFactory);
		writer.setUsePersist(false);
		return writer;
	}
}
