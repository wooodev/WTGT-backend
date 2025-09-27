package com.swyp.catsgotogedog.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class InvalidTokenException extends CatsgotogedogException {

	public InvalidTokenException(ErrorCode errorCode) {
		super(errorCode);
	}
}
