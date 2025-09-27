package com.swyp.catsgotogedog.global.exception;

import lombok.Getter;

@Getter
public class UnAuthorizedAccessException extends RuntimeException{

	private final ErrorCode errorCode;

	public UnAuthorizedAccessException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
