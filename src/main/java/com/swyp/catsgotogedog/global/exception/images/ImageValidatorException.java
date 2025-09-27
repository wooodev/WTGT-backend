package com.swyp.catsgotogedog.global.exception.images;

import com.swyp.catsgotogedog.global.exception.CatsgotogedogException;
import com.swyp.catsgotogedog.global.exception.ErrorCode;

public class ImageValidatorException extends CatsgotogedogException {

    public ImageValidatorException(ErrorCode errorCode) {
        super(errorCode);
    }
}
