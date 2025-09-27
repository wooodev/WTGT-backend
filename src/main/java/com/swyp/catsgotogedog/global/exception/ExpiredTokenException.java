package com.swyp.catsgotogedog.global.exception;

import lombok.Getter;

@Getter
public class ExpiredTokenException extends CatsgotogedogException {

	public ExpiredTokenException(ErrorCode errorCode) {
		super(errorCode);
	}
}
