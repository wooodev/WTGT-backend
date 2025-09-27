package com.swyp.catsgotogedog.global.exception.images;

import com.swyp.catsgotogedog.global.exception.ErrorCode;

public class InvalidImageException extends ImageValidatorException {

    public InvalidImageException(ErrorCode errorCode) {
        super(errorCode);
    }
}
