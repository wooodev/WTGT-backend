package com.swyp.catsgotogedog.common.util.image.validator;

import lombok.Getter;

@Getter
public enum ImageUploadType {
    PROFILE(1),
    REVIEW(3),
    GENERAL(10);

    private final int maxFiles;

    ImageUploadType(int maxFiles) {
        this.maxFiles = maxFiles;
    }

}