package com.swyp.catsgotogedog;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = { "com.swyp", "com.batch" })
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class CatsgotogedogApplication implements CommandLineRunner {

	private final JobLauncher jobLauncher;
	private final ApplicationContext applicationContext;
	// private final Job contentDataFetchStep;
	// private final Job categoryCodeFetchStep;

	public static void main(String[] args) {
		SpringApplication.run(CatsgotogedogApplication.class, args);
	}

	@Scheduled(cron = "0 0 1 * * ?")
	public void runBatch() throws Exception {
		Job contentBatchJob = (Job) applicationContext.getBean("contentBatchJob");

		// category job (서버 최초 실행시에만 실행)
		JobParameters jobParameters = new JobParametersBuilder()
			.addLong("time", System.currentTimeMillis())
			.toJobParameters();

		//content job
		jobLauncher.run(contentBatchJob, jobParameters);
		log.info(">> CommandLineRunner: content 배치 수동 실행 완료");

	}

	@Override
	public void run(String... args) throws Exception {
		Job categoryCodeBatchJob = (Job) applicationContext.getBean("categoryCodeBatchJob");
		Job contentBatchJob = (Job) applicationContext.getBean("contentBatchJob");

		// category job (서버 최초 실행시에만 실행)
		JobParameters jobParameters = new JobParametersBuilder()
			.addLong("time", System.currentTimeMillis())
			.toJobParameters();
		jobLauncher.run(categoryCodeBatchJob, jobParameters);
		log.info(">> CommandLineRunner: categoryCode 배치 수동 실행 완료");

		//content job
		jobLauncher.run(contentBatchJob, jobParameters);
		log.info(">> CommandLineRunner: content 배치 수동 실행 완료");

	}
}
