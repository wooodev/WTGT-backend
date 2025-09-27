package com.swyp.catsgotogedog.common.util.image.validator;

import com.swyp.catsgotogedog.global.exception.ErrorCode;
import com.swyp.catsgotogedog.global.exception.images.ImageNotFoundException;
import com.swyp.catsgotogedog.global.exception.images.InvalidImageException;
import com.swyp.catsgotogedog.global.exception.images.ImageLimitExceededException;
import org.apache.tika.Tika;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
public class DefaultImageValidator implements ImageValidator {

    private final Tika tika = new Tika();
    private final Set<String> allowedExtensions = Set.of("jpg", "jpeg", "png");
    private final Set<String> allowedMimeTypes = Set.of("image/jpeg", "image/png");

    /**
     * 단일 이미지 파일 검증
     * @param file 검증할 이미지 파일
     * @param uploadType 업로드 타입 (최대 파일 수 제한용)
     */
    @Override
    public void validate(MultipartFile file, ImageUploadType uploadType) {
        validateFileNotNull(file);
        validateFileNameAndExtension(file);
        validateImageFormat(file);
    }

    /**
     * 다중 이미지 파일 검증
     * @param files 검증할 이미지 파일 리스트
     * @param uploadType 업로드 타입 (최대 파일 수 제한용)
     */
    @Override
    public void validate(List<MultipartFile> files, ImageUploadType uploadType) {
        validateFilesNotNull(files);
        validateFileCount(files, uploadType);

        // 각 파일에 대해 개별 검증 수행 (하나라도 실패하면 전체 실패)
        for (MultipartFile file : files) {
            validate(file, uploadType);
        }
    }

    private void validateFileNotNull(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImageNotFoundException(ErrorCode.IMAGE_NOT_FOUND);
        }
    }

    private void validateFileNameAndExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            throw new InvalidImageException(ErrorCode.INVALID_IMAGE_NAME);
        }

        String baseFilename = getBaseFilename(originalFilename);
        if (baseFilename == null || baseFilename.isBlank()) {
            throw new InvalidImageException(ErrorCode.INVALID_IMAGE_NAME);
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!allowedExtensions.contains(extension)) {
            throw new InvalidImageException(ErrorCode.INVALID_IMAGE_EXTENSION);
        }
    }

    private void validateImageFormat(MultipartFile file) {
        try {
            // Apache Tika를 사용하여 실제 파일 내용의 MIME 타입 검증
            String detectedMimeType = tika.detect(file.getInputStream());
            if (!allowedMimeTypes.contains(detectedMimeType)) {
                throw new InvalidImageException(ErrorCode.INVALID_IMAGE_FORMAT);
            }

            // 추가 검증: 파일 확장자와 실제 MIME 타입 일치 여부 확인
            validateExtensionMimeTypeConsistency(file, detectedMimeType);

        } catch (IOException e) {
            throw new InvalidImageException(ErrorCode.STREAM_IO_EXCEPTION);
        }
    }

    private void validateExtensionMimeTypeConsistency(MultipartFile file, String detectedMimeType) {
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename).toLowerCase();

        boolean isConsistent = switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg".equals(detectedMimeType);
            case "png" -> "image/png".equals(detectedMimeType);
            default -> false;
        };

        if (!isConsistent) {
            throw new InvalidImageException(ErrorCode.INVALID_IMAGE_FORMAT);
        }
    }

    private void validateFilesNotNull(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            throw new ImageNotFoundException(ErrorCode.IMAGE_NOT_FOUND);
        }
    }

    private void validateFileCount(List<MultipartFile> files, ImageUploadType uploadType) {
        if (files.size() > uploadType.getMaxFiles()) {
            throw new ImageLimitExceededException(ErrorCode.IMAGE_LIMIT_EXCEEDED);
        }
    }

    private String getBaseFilename(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(0, lastDotIndex);
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1);
    }
}
