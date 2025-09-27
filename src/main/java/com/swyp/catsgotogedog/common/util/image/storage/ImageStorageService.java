package com.swyp.catsgotogedog.common.util.image.storage;

import com.swyp.catsgotogedog.common.util.image.storage.dto.ImageInfo;
import com.swyp.catsgotogedog.common.util.image.validator.ImageUploadType;
import com.swyp.catsgotogedog.common.util.image.validator.ImageValidator;
import com.swyp.catsgotogedog.global.exception.*;
import com.swyp.catsgotogedog.global.exception.images.ImageNotFoundException;
import com.swyp.catsgotogedog.global.exception.images.ImageUploadException;
import com.swyp.catsgotogedog.global.exception.images.ImageLimitExceededException;
import io.awspring.cloud.s3.ObjectMetadata;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class ImageStorageService {

    private final S3Template s3Template;
    private final ObjectMetadataProvider objectMetadataProvider;
    private final ImageValidator imageValidator;
    private final String bucketName;

    private final int MAX_FILE_COUNT = 10;

    public ImageStorageService(S3Template s3Template,
                               ObjectMetadataProvider objectMetadataProvider,
                               ImageValidator imageValidator,
                               @Value("${spring.cloud.aws.s3.bucket}") String bucketName) {

        this.s3Template = s3Template;
        this.objectMetadataProvider = objectMetadataProvider;
        this.imageValidator = imageValidator;
        this.bucketName = bucketName;
    }

    /**
     * 다중 이미지 업로드
     * @param files MultipartFile list
     * @param uploadType 업로드 타입 (프로필 업로드, 리뷰 업로드 등 개수 제한용)
     * @return List&lt;ImageInfo&gt;
     */
    public List<ImageInfo> upload(List<MultipartFile> files, ImageUploadType uploadType) {
        return upload(files, "", uploadType);
    }

    /**
     * 다중 이미지 업로드
     * @param files MultipartFile list
     * @param path 업로드 경로
     * @param uploadType 업로드 타입 (프로필 업로드, 리뷰 업로드 등 개수 제한용)
     * @return List&lt;ImageInfo&gt;
     */
    public List<ImageInfo> upload(List<MultipartFile> files, String path, ImageUploadType uploadType) {
        validateFiles(files,uploadType);
        return files.stream()
                .map(file -> doUpload(file, path))
                .collect(Collectors.toList());
    }

    /**
     * 단일 이미지 업로드
     * @param file MultipartFile
     * @param uploadType 업로드 타입 (프로필 업로드, 리뷰 업로드 등 개수 제한용)
     * @return List&lt;ImageInfo&gt;
     */
    public List<ImageInfo> upload(MultipartFile file, ImageUploadType uploadType) {
        return upload(file, "", uploadType);
    }

    /**
     * 단일 이미지 업로드
     * @param file MultipartFile
     * @param path 업로드 경로
     * @param uploadType 업로드 타입 (프로필 업로드, 리뷰 업로드 등 개수 제한용)
     * @return List&lt;ImageInfo&gt;
     */
    public List<ImageInfo> upload(MultipartFile file, String path, ImageUploadType uploadType) {
        validateFile(file, uploadType);
        return Collections.singletonList(doUpload(file, path));
    }

    /**
     * 이미지 삭제
     * @param key image key
     */
    public void delete(String key) {
        validateKey(key);
        doDelete(key);
    }

    /**
     * 다중 이미지 삭제
     * @param keys list of image keys
     */
    public void delete(List<String> keys) {
        validateKeyList(keys);
        keys.forEach(this::doDelete);
    }

    private ImageInfo doUpload(MultipartFile file, String path) {
        String key = genKey(file, path);
        ObjectMetadata metadata = objectMetadataProvider.createPublicReadMetadata(file);

        try (InputStream stream = file.getInputStream()) {
            S3Resource resource = s3Template.upload(bucketName, key, stream, metadata);
            return new ImageInfo(resource.getFilename(), resource.getURL().toString());
        } catch (IOException e) { // Stream Exception
            throw new ImageUploadException(ErrorCode.STREAM_IO_EXCEPTION);
        } catch (Exception e) { // S3Exception 등
            throw new ImageUploadException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    // S3에서 객체 삭제
    private void doDelete(String key) {
        s3Template.deleteObject(bucketName, key);
    }

    // MIME 타입 검사 등 Tika를 사용한 바이너리 검사 기능 별도로 개발 필요
    private void validateFiles(List<MultipartFile> files, ImageUploadType uploadType) {
        imageValidator.validate(files, uploadType);
    }

    // MIME 타입 검사 등 Tika를 사용한 바이너리 검사 기능 별도로 개발 필요
    private void validateFile(MultipartFile file, ImageUploadType uploadType) {
        imageValidator.validate(file, uploadType);
    }

    private void validateKeyList(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            throw new ImageKeyNotFoundException(ErrorCode.IMAGE_KEY_NOT_FOUND);
        }
        // 전체 키 리스트의 유효성을 먼저 검사
        if (keys.stream().anyMatch(key -> key == null || key.isBlank())) {
            throw new ImageKeyNotFoundException(ErrorCode.IMAGE_KEY_NOT_FOUND);
        }
    }

    private void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new ImageKeyNotFoundException(ErrorCode.IMAGE_KEY_NOT_FOUND);
        }
    }

    // 파일 이름과 UUID를 조합하여 고유한 키 생성
    private String genKey(MultipartFile file, String path) {
        return path + UUID.randomUUID() + getFileExtension(file.getOriginalFilename());
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        return "." + filename.substring(lastDotIndex + 1).toLowerCase();
    }

}
