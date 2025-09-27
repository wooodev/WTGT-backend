package com.swyp.catsgotogedog.global.exception.images;

import com.swyp.catsgotogedog.global.exception.CatsgotogedogException;
import com.swyp.catsgotogedog.global.exception.ErrorCode;

public class ImageUploadException extends CatsgotogedogException {

    public ImageUploadException(ErrorCode errorCode) {
        super(errorCode);
    }

}