package com.swyp.catsgotogedog.global.exception;

import lombok.Getter;

@Getter
public class NotAllowedMethodException extends CatsgotogedogException {

	public NotAllowedMethodException(ErrorCode errorCode) {
		super(errorCode);
	}
}
