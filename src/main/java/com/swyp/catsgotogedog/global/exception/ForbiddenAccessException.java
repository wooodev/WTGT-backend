package com.swyp.catsgotogedog.global.exception;

import lombok.Getter;

@Getter
public class ForbiddenAccessException extends CatsgotogedogException {

	public ForbiddenAccessException(ErrorCode errorCode) {
		super(errorCode);
	}
}
