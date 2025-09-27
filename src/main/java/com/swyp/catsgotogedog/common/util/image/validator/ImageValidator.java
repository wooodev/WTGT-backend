package com.swyp.catsgotogedog.common.util.image.validator;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ImageValidator {

    void validate(MultipartFile file, ImageUploadType uploadType);

    void validate(List<MultipartFile> files, ImageUploadType uploadType);
}
