package com.batch.listener;

import org.springframework.batch.core.SkipListener;
import org.springframework.stereotype.Component;

import com.swyp.catsgotogedog.content.domain.entity.Content;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomSkipListener implements SkipListener<Content, Object> {

	@Override
	public void onSkipInRead(Throwable t) {
		log.warn("[스킵] Reader 단계에서 스킵 사유 : {}", t.getMessage());
	}

	@Override
	public void onSkipInWrite(Object item, Throwable t) {
		log.warn("[스킵] Writer 단계에서 스킵 발생, Item: {}, 사유 : {}", item, t.getMessage());
	}

	@Override
	public void onSkipInProcess(Content item, Throwable t) {
		log.warn("[SKIP] Processor 단계에서 스킵 발생, ContentId : {}, 사유 : {}, {}", item.getContentId(), t.getMessage(), t.getStackTrace());
	}
}
