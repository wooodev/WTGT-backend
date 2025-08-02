package com.batch.listener;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;


import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomStepExecutionListener implements StepExecutionListener {

	@Override
	public void beforeStep(StepExecution stepExecution) {
		log.info("[배치 스탭 시작] 스탭 이름 : {}, Job 이름 : {}",
			stepExecution.getStepName(), stepExecution.getJobExecution().getJobInstance().getJobName());
	}

	@Override
	public ExitStatus afterStep(StepExecution stepExecution) {
		log.info("[배치 스탭 종료] 스탭 이름 : {}, 상태 : {}, 읽기 : {}, 쓰기 : {}, 스킵 : {}, 실패 : {}",
			stepExecution.getStepName(),
			stepExecution.getStatus(),
			stepExecution.getReadCount(),
			stepExecution.getWriteCount(),
			stepExecution.getSkipCount(),
			stepExecution.getFailureExceptions().size());
		if (stepExecution.getStatus().isUnsuccessful()) {
			log.error("{} 스텝이 {} 상태로 완료되었습니다. 이유 : {}",
				stepExecution.getStepName(),
				stepExecution.getStatus(),
				stepExecution.getFailureExceptions());
		}
		return stepExecution.getExitStatus();
	}
}
