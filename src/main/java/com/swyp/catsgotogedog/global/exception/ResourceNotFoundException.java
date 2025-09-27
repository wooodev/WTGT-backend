package com.swyp.catsgotogedog.global.exception;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends CatsgotogedogException {

	public ResourceNotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
