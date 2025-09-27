package com.swyp.catsgotogedog.global.exception;

import lombok.Getter;

@Getter
public class CatsgotogedogException extends RuntimeException{

	private final ErrorCode errorCode;

	public CatsgotogedogException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
