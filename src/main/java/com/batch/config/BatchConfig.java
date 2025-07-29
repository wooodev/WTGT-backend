package com.batch.config;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.ResourceAccessException;

import com.batch.dto.AreaBasedListResponse;
import com.batch.listener.CustomJobExecutionListener;
import com.batch.listener.CustomStepExecutionListener;
import com.batch.processor.AreaBasedListItemProcessor;
import com.batch.processor.DetailCommonProcessor;
import com.batch.processor.DetailImageProcessor;
import com.batch.reader.AreaBasedListApiReader;
import com.batch.reader.DetailImageReader;
import com.batch.tasklet.CategoryFetchTasklet;
import com.batch.writer.ContentImageWriter;
import com.batch.writer.DetailCommonWriter;
import com.batch.writer.ItemWriterConfig;
import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.content.domain.entity.ContentImage;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class BatchConfig {

	private final EntityManagerFactory entityManagerFactory;
	private final PlatformTransactionManager transactionManager;
	private final CustomJobExecutionListener customJobExecutionListener;
	private final CustomStepExecutionListener customStepExecutionListener;
	private final JobRepository jobRepository;
	private final CategoryFetchTasklet categoryFetchTasklet;

	// Reader
	private final JpaPagingItemReader<Content> detailImageContentReader;
	private final AreaBasedListApiReader contentReader;
	private final JpaPagingItemReader<Content> detailCommonItemReader;

	// Writer
	private final ItemWriterConfig itemWriterConfig;
	private final ContentImageWriter contentImageWriter;
	private final DetailCommonWriter detailCommonWriter;

	// Processor
	private final DetailImageProcessor detailImageProcessor;
	private final AreaBasedListItemProcessor contentProcessor;
	private final DetailCommonProcessor detailCommonProcessor;

	private final int CHUNK_SIZE = 100;


	// 메인 JOB 컨텐츠 > 이미지 > 디테일
	@Bean
	public Job contentBatchJob() {
		log.info("Configuring contentBatchJob...");
		return new JobBuilder("contentBatchJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.listener(customJobExecutionListener)
			.start(contentDataFetchStep())
			.next(detailCommonFetchStep())
			//.next(detailImageFetchStep())
			.build();
	}

	// content step
	@Bean
	public Step contentDataFetchStep() {
		log.info("Configuring contentDataFetchStep...");
		return new StepBuilder("contentDataFetchStep", jobRepository)
			.<AreaBasedListResponse.Item, Content>chunk(CHUNK_SIZE, transactionManager)
			.reader(contentReader)
			.processor(contentProcessor)
			.writer(itemWriterConfig.step1ContentWriter(entityManagerFactory))
			.faultTolerant()
				.skipLimit(2000)
				.skip(Exception.class)
				.retryLimit(3)
				.retry(ResourceAccessException.class)
			.listener(customStepExecutionListener)
			.build();
	}

	// category Step
	@Bean
	public Step categoryCodeFetchStep() {
		log.info("Configuring categoryCodeFetchStep...");
		return new StepBuilder("categoryCodeFetchStep", jobRepository)
			.tasklet(categoryFetchTasklet, transactionManager)
			.listener(customStepExecutionListener)
			.build();
	}

	// category Job
	@Bean
	public Job categoryCodeBatchJob() {
		log.info("Configuring categoryCodeBatchJob...");
		return new JobBuilder("categoryCodeBatchJob", jobRepository)
			.incrementer(new RunIdIncrementer())
			.listener(customJobExecutionListener)
			.start(categoryCodeFetchStep())
			.build();
	}

	// 이미지 스텝
	@Bean
	public Step detailImageFetchStep() {
		return new StepBuilder("detailImageFetchStep", jobRepository)
			.<Content, List<ContentImage>>chunk(100, transactionManager)
			.reader(detailImageContentReader)
			.processor(detailImageProcessor)
			.writer(contentImageWriter)
			.faultTolerant()
				.skipLimit(2000)
				.skip(Exception.class)
				.retry(ResourceAccessException.class)
			.listener(customStepExecutionListener)
			.build();
	}

	// content overview 스텝
	@Bean
	public Step detailCommonFetchStep() {
		return new StepBuilder("detailCommonFetchStep", jobRepository)
			.<Content, Content>chunk(100, transactionManager)
			.reader(detailCommonItemReader)
			.processor(detailCommonProcessor)
			.writer(detailCommonWriter)
			.faultTolerant()
				.skipLimit(2000)
				.skip(Exception.class)
				.retry(ResourceAccessException.class)
			.listener(customStepExecutionListener)
			.build();
	}
}
