package com.swyp.catsgotogedog.global.exception;

import lombok.Getter;

@Getter
public class ContentNotFoundException extends CatsgotogedogException {

	public ContentNotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
