package com.batch.listener;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomJobExecutionListener implements JobExecutionListener {
	@Override
	public void beforeJob(JobExecution jobExecution) {
		log.info("[배치 Job 시작] Job 이름 : {}, Job Id : {}",
			jobExecution.getJobInstance().getJobName(), jobExecution.getId());
		log.info("Job Parameter : {}", jobExecution.getJobParameters());
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		log.info("[배치 JOB 종료] Job 이름: {}, Job Id : {}, 상태 : {}",
			jobExecution.getJobInstance().getJobName(), jobExecution.getJobId(), jobExecution.getStatus());

		if (jobExecution.getStatus().isUnsuccessful()) {
			log.error("Job {} 이 상태 {} 로 완료 되었습니다. 실패 : {}",
				jobExecution.getJobInstance().getJobName(), jobExecution.getStatus(), jobExecution.getAllFailureExceptions());
		}
	}
}
