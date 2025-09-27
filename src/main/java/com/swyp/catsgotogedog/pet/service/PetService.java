package com.swyp.catsgotogedog.pet.service;

import com.swyp.catsgotogedog.User.domain.entity.User;
import com.swyp.catsgotogedog.User.repository.UserRepository;
import com.swyp.catsgotogedog.common.util.image.storage.ImageStorageService;
import com.swyp.catsgotogedog.common.util.image.storage.dto.ImageInfo;
import com.swyp.catsgotogedog.common.util.image.validator.ImageUploadType;
import com.swyp.catsgotogedog.global.exception.ErrorCode;
import com.swyp.catsgotogedog.global.exception.ForbiddenAccessException;
import com.swyp.catsgotogedog.global.exception.MemberNotFoundException;
import com.swyp.catsgotogedog.global.exception.PetLimitExceededException;
import com.swyp.catsgotogedog.global.exception.ResourceNotFoundException;
import com.swyp.catsgotogedog.global.exception.images.ImageUploadException;
import com.swyp.catsgotogedog.pet.domain.entity.Pet;
import com.swyp.catsgotogedog.pet.domain.entity.PetSize;
import com.swyp.catsgotogedog.pet.domain.request.PetProfileRequest;
import com.swyp.catsgotogedog.pet.repository.PetRepository;
import com.swyp.catsgotogedog.pet.repository.PetSizeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PetService {

    private final PetRepository petRepository;
    private final PetSizeRepository petSizeRepository;
    private final UserRepository userRepository;
    private final ImageStorageService imageStorageService;

    private final String DEFAULT_IMAGE_URL = "https://kr.object.ncloudstorage.com/catsgotogedogbucket/profile/default_pet_image.png";

    public List<Pet> getAllPets(String userId) {
        return petRepository.findAllByUser_UserIdOrderByPetId(Integer.parseInt(userId));
    }

    public void create(String userId, PetProfileRequest petProfileRequest) {
        User user = findUserById(userId);
        validatePetLimit(userId);

        PetSize petSize = findPetSizeBySize(petProfileRequest.getSize());

        String imageUrl = DEFAULT_IMAGE_URL;
        String imageFilename = null;

        // 이미지 업로드 처리 (이미지가 있는 경우)
        if (hasImage(petProfileRequest)) {
            ImageInfo imageInfo = uploadImage(petProfileRequest);
            imageUrl = imageInfo.url();
            imageFilename = imageInfo.key();
        }

        Pet pet = Pet.builder()
                .user(user)
                .name(petProfileRequest.getName())
                .gender(petProfileRequest.getGender().charAt(0))
                .birth(petProfileRequest.getBirth())
                .type(petProfileRequest.getType())
                .fierceDog(petProfileRequest.isFierceDog())
                .sizeId(petSize)
                .imageUrl(imageUrl)
                .imageFilename(imageFilename)
                .build();

        petRepository.save(pet);
    }

    public void updateById(String userId, int petId, PetProfileRequest petProfileRequest) {

        Pet pet = findPetByIdAndUserId(petId, userId);
        PetSize petSize = findPetSizeBySize(petProfileRequest.getSize());

        // 새 이미지가 업로드된 경우 기존 이미지 삭제 후 새 이미지 업로드
        if (hasImage(petProfileRequest)) {
            deleteExistingImageIfExists(pet);
            ImageInfo imageInfo = uploadImage(petProfileRequest);
            pet.setImageUrl(imageInfo.url());
            pet.setImageFilename(imageInfo.key());
        }

        // 펫 정보 업데이트
        pet.setName(petProfileRequest.getName());
        pet.setGender(petProfileRequest.getGender().charAt(0));
        pet.setBirth(petProfileRequest.getBirth());
        pet.setType(petProfileRequest.getType());
        pet.setFierceDog(petProfileRequest.isFierceDog());
        pet.setSizeId(petSize);

        petRepository.save(pet);
    }

    public void deleteById(String userId, int petId) {
        Pet pet = findPetByIdAndUserId(petId, userId);

        deleteExistingImageIfExists(pet);
        petRepository.delete(pet);
    }

    private User findUserById(String userId) {
        return userRepository.findById(Integer.parseInt(userId))
                .orElseThrow(() -> new MemberNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private void validatePetLimit(String userId) {
        int petCount = petRepository.countByUser_UserId(Integer.parseInt(userId));
        if (petCount >= 10) {
            throw new PetLimitExceededException(ErrorCode.PET_LIMIT_EXCEEDED);
        }
    }

    private Pet findPetByIdAndUserId(int petId, String userId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PET_NOT_FOUND));

        if (pet.getUser().getUserId() != Integer.parseInt(userId)) {
            throw new ForbiddenAccessException(ErrorCode.FORBIDDEN_ACCESS);
        }

        return pet;
    }

    private PetSize findPetSizeBySize(String size) {
        return petSizeRepository.findBySize(size)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.PET_SIZE_NOT_FOUND));
    }

    private boolean hasImage(PetProfileRequest request) {
        return request.getImage() != null && !request.getImage().isEmpty();
    }

    private ImageInfo uploadImage(PetProfileRequest request) {
        try {
            List<ImageInfo> imageInfos = imageStorageService.upload(
                    request.getImage(), "pet_profile/", ImageUploadType.PROFILE);
            return imageInfos.get(0);
        } catch (Exception e) {
            throw new ImageUploadException(ErrorCode.IMAGE_UPLOAD_FAILED);
        }
    }

    private void deleteExistingImageIfExists(Pet pet) {
        if (StringUtils.hasText(pet.getImageFilename())) {
            imageStorageService.delete(pet.getImageFilename());
            pet.setImageFilename(null);
        }
        pet.setImageUrl(DEFAULT_IMAGE_URL);
    }
}
