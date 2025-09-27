package com.swyp.catsgotogedog.global.exception;

import lombok.Getter;

@Getter
public class PetLimitExceededException extends CatsgotogedogException {

    public PetLimitExceededException(ErrorCode errorCode) {
        super(errorCode);
    }
}
