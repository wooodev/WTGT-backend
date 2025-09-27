package com.swyp.catsgotogedog.global.exception;

import lombok.Getter;

@Getter
public class MemberNotFoundException extends CatsgotogedogException {

	public MemberNotFoundException(ErrorCode errorCode) {
		super(errorCode);
	}
}
