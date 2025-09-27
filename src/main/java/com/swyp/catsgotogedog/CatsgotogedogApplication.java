package com.swyp.catsgotogedog;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@EnableAsync
@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = { "com.swyp", "com.batch" })
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class CatsgotogedogApplication implements CommandLineRunner {

	private final JobLauncher jobLauncher;
	private final ApplicationContext applicationContext;

	public static void main(String[] args)  {
		SpringApplication.run(CatsgotogedogApplication.class, args);
	}

	@Scheduled(cron = "0 20 2 * * ?")
	public void runBatch() throws Exception {
		log.info("############# 02시 데이터 마이그레이션 배치 진행 ##############");
		Job categoryCodeBatchJob = (Job) applicationContext.getBean("categoryCodeBatchJob");
		Job contentBatchJob = (Job) applicationContext.getBean("contentBatchJob");

		JobParameters jobParameters = new JobParametersBuilder()
			.addLong("time", System.currentTimeMillis())
			.toJobParameters();
		jobLauncher.run(categoryCodeBatchJob, jobParameters);
		log.info(">> 02:00 AM CategoryCode 배치 스케쥴러 작동");

		jobLauncher.run(contentBatchJob, jobParameters);
		log.info(">> 02:00 AM Content Fetch 배치 스케쥴러 작동");

	}

	@Override
	public void run(String... args) throws Exception {
		// log.info("############# 02시 데이터 마이그레이션 배치 진행 ##############");
		// Job categoryCodeBatchJob = (Job) applicationContext.getBean("categoryCodeBatchJob");
		// Job contentBatchJob = (Job) applicationContext.getBean("contentBatchJob");
		//
		// JobParameters jobParameters = new JobParametersBuilder()
		// 	.addLong("time", System.currentTimeMillis())
		// 	.toJobParameters();
		// jobLauncher.run(categoryCodeBatchJob, jobParameters);
		// log.info(">> 02:00 AM CategoryCode 배치 스케쥴러 작동");
		//
		// jobLauncher.run(contentBatchJob, jobParameters);
		// log.info(">> 01:00 AM Content Fetch 배치 스케쥴러 작동");
	}
}
