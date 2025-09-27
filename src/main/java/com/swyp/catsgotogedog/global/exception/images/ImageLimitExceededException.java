package com.swyp.catsgotogedog.global.exception.images;

import com.swyp.catsgotogedog.global.exception.ErrorCode;

public class ImageLimitExceededException extends ImageValidatorException {

    public ImageLimitExceededException(ErrorCode errorCode) {
        super(errorCode);
    }
}
