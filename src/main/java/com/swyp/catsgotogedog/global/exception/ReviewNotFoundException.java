package com.swyp.catsgotogedog.global.exception;

import lombok.Getter;

@Getter
public class ReviewNotFoundException extends CatsgotogedogException {

	public ReviewNotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
