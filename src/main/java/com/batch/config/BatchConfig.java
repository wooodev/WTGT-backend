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
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.ResourceAccessException;

import com.batch.dto.AreaBasedListResponse;
import com.batch.dto.DetailInfoProcessResult;
import com.batch.dto.DetailIntroProcessResult;
import com.batch.listener.CustomJobExecutionListener;
import com.batch.listener.CustomSkipListener;
import com.batch.listener.CustomStepExecutionListener;
import com.batch.processor.AreaBasedListItemProcessor;
import com.batch.processor.DetailCommonProcessor;
import com.batch.processor.DetailImageProcessor;
import com.batch.processor.DetailInfoProcessor;
import com.batch.processor.DetailIntroProcessor;
import com.batch.processor.DetailPetTourProcessor;
import com.batch.reader.AreaBasedListApiReader;
import com.batch.tasklet.CategoryFetchTasklet;
import com.batch.writer.ContentImageWriter;
import com.batch.writer.DetailCommonWriter;
import com.batch.writer.DetailInfoWriter;
import com.batch.writer.DetailIntroWriter;
import com.batch.writer.DetailPetTourWriter;
import com.batch.writer.ItemWriterConfig;
import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.content.domain.entity.ContentImage;
import com.swyp.catsgotogedog.pet.domain.entity.PetGuide;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class BatchConfig {

	private final EntityManagerFactory entityManagerFactory;
	private final PlatformTransactionManager transactionManager;
	private final JobRepository jobRepository;
	private final CategoryFetchTasklet categoryFetchTasklet;

	// Listener
	private final CustomJobExecutionListener customJobExecutionListener;
	private final CustomStepExecutionListener customStepExecutionListener;
	private final CustomSkipListener customSkipListener;

	// Reader
	private final JpaPagingItemReader<Content> detailImageContentReader;
	private final AreaBasedListApiReader contentReader;
	private final JpaPagingItemReader<Content> detailCommonItemReader;
	private final JpaPagingItemReader<Content> detailPetTourItemReader;
	private final JpaPagingItemReader<Content> detailInfoItemReader;
	private final JpaCursorItemReader<Content> sightsInformationItemReader;
	private final JpaCursorItemReader<Content> lodgeInformationItemReader;
	private final JpaCursorItemReader<Content> festivalInformationItemReader;
	private final JpaCursorItemReader<Content> restaurantInformationItemReader;

	// Writer
	private final ItemWriterConfig itemWriterConfig;
	private final ContentImageWriter contentImageWriter;
	private final DetailCommonWriter detailCommonWriter;
	private final DetailPetTourWriter detailPetTourWriter;
	private final DetailInfoWriter detailInfoWriter;
	private final DetailIntroWriter detailIntroWriter;

	// Processor
	private final DetailImageProcessor detailImageProcessor;
	private final AreaBasedListItemProcessor contentProcessor;
	private final DetailCommonProcessor detailCommonProcessor;
	private final DetailPetTourProcessor detailPetTourProcessor;
	private final DetailInfoProcessor detailInfoProcessor;
	private final DetailIntroProcessor detailIntroProcessor;

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
			.next(detailImageFetchStep())
			.next(petGuideFetchStep())
			.next(detailInfoFetchStep())
			.next(detailIntroSightsFetchStep())
			.next(detailIntroLodgeFetchStep())
			.next(detailIntroRestaurantFetchStep())
			.next(detailIntroFestivalFetchStep())
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
			.listener(customStepExecutionListener)
			.faultTolerant()
				.skipLimit(2000)
				.skip(Exception.class)
				.retry(ResourceAccessException.class)
			.listener(customSkipListener)
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
			.<Content, List<ContentImage>>chunk(CHUNK_SIZE, transactionManager)
			.reader(detailImageContentReader)
			.processor(detailImageProcessor)
			.writer(contentImageWriter)
			.listener(customStepExecutionListener)
			.faultTolerant()
				.skipLimit(2000)
				.skip(Exception.class)
				.retry(ResourceAccessException.class)
			.listener(customSkipListener)
			.build();
	}

	// content overview 스텝
	@Bean
	public Step detailCommonFetchStep() {
		return new StepBuilder("detailCommonFetchStep", jobRepository)
			.<Content, Content>chunk(CHUNK_SIZE, transactionManager)
			.reader(detailCommonItemReader)
			.processor(detailCommonProcessor)
			.writer(detailCommonWriter)
			.listener(customStepExecutionListener)
			.faultTolerant()
				.skipLimit(2000)
				.skip(Exception.class)
				.retry(ResourceAccessException.class)
			.listener(customSkipListener)
			.build();
	}

	// PetGuide 스텝
	@Bean
	public Step petGuideFetchStep() {
		return new StepBuilder("petGuideFetchStep", jobRepository)
			.<Content, PetGuide>chunk(CHUNK_SIZE, transactionManager)
			.reader(detailPetTourItemReader)
			.processor(detailPetTourProcessor)
			.writer(detailPetTourWriter)
			.listener(customStepExecutionListener)
			.faultTolerant()
				.skipLimit(2000)
				.skip(Exception.class)
				.retry(ResourceAccessException.class)
			.listener(customSkipListener)
			.build();
	}

	// detailInfo 스텝
	@Bean
	public Step detailInfoFetchStep() {
		return new StepBuilder("detailInfoFetchStep", jobRepository)
			.<Content, DetailInfoProcessResult>chunk(CHUNK_SIZE, transactionManager)
			.reader(detailInfoItemReader)
			.processor(detailInfoProcessor)
			.writer(detailInfoWriter)
			.listener(customStepExecutionListener)
			.faultTolerant()
				.skipLimit(2000)
				.skip(Exception.class)
				.retry(ResourceAccessException.class)
			.listener(customSkipListener)
			.build();
	}

	// detailIntro 스텝
	@Bean
	public Step detailIntroSightsFetchStep() {
		return new StepBuilder("detailIntroSightsFetchStep", jobRepository)
			.<Content, DetailIntroProcessResult>chunk(CHUNK_SIZE, transactionManager)
			.reader(sightsInformationItemReader)
			.processor(detailIntroProcessor)
			.writer(detailIntroWriter)
			.listener(customStepExecutionListener)
			.faultTolerant()
			.skipLimit(2000)
			.skip(Exception.class)
			.retry(ResourceAccessException.class)
			.listener(customSkipListener)
			.build();
	}
	// lodge 청크이슈로 1로 설정
	@Bean
	public Step detailIntroLodgeFetchStep() {
		return new StepBuilder("detailIntroLodgeFetchStep", jobRepository)
			.<Content, DetailIntroProcessResult>chunk(1, transactionManager)
			.reader(lodgeInformationItemReader)
			.processor(detailIntroProcessor)
			.writer(detailIntroWriter)
			.listener(customStepExecutionListener)
			.faultTolerant()
				.skipLimit(2000)
				.skip(Exception.class)
				.retry(ResourceAccessException.class)
			.listener(customSkipListener)
			.build();
	}
	@Bean
	public Step detailIntroRestaurantFetchStep() {
		return new StepBuilder("detailIntroRestaurantFetchStep", jobRepository)
			.<Content, DetailIntroProcessResult>chunk(CHUNK_SIZE, transactionManager)
			.reader(restaurantInformationItemReader)
			.processor(detailIntroProcessor)
			.writer(detailIntroWriter)
			.listener(customStepExecutionListener)
			.faultTolerant()
				.skipLimit(2000)
				.skip(Exception.class)
				.retry(ResourceAccessException.class)
			.listener(customSkipListener)
			.build();
	}
	@Bean
	public Step detailIntroFestivalFetchStep() {
		return new StepBuilder("detailIntroFestivalFetchStep", jobRepository)
			.<Content, DetailIntroProcessResult>chunk(CHUNK_SIZE, transactionManager)
			.reader(festivalInformationItemReader)
			.processor(detailIntroProcessor)
			.writer(detailIntroWriter)
			.listener(customStepExecutionListener)
			.faultTolerant()
				.skipLimit(2000)
				.skip(Exception.class)
				.retry(ResourceAccessException.class)
			.listener(customSkipListener)
			.build();
	}
}
