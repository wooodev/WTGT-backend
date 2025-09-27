package com.swyp.catsgotogedog.global.exception.images;

import com.swyp.catsgotogedog.global.exception.ErrorCode;

public class ImageNotFoundException extends ImageValidatorException {

    public ImageNotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }
}
