package com.swyp.catsgotogedog.pet.controller;

import com.swyp.catsgotogedog.global.CatsgotogedogApiResponse;
import com.swyp.catsgotogedog.pet.domain.entity.Pet;
import com.swyp.catsgotogedog.pet.domain.request.PetProfileRequest;
import com.swyp.catsgotogedog.pet.domain.response.PetProfileResponse;
import com.swyp.catsgotogedog.pet.service.PetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/pet")
@Slf4j
public class PetController implements PetControllerSwagger {

    private final PetService petService;

    @GetMapping("/profile")
    public ResponseEntity<CatsgotogedogApiResponse<?>> getAllProfiles(
            @AuthenticationPrincipal String userId) {
        List<Pet> pets = petService.getAllPets(userId);
        List<PetProfileResponse> response = pets.stream()
                .map(PetProfileResponse::from)
                .toList();
        return ResponseEntity.ok(
                CatsgotogedogApiResponse.success("반려동물 프로필 목록 조회 성공", response));
    }

    @PostMapping("/profile")
    public ResponseEntity<CatsgotogedogApiResponse<?>> createProfile(
            @AuthenticationPrincipal String userId,
            @Valid @ModelAttribute PetProfileRequest petProfileRequest) {
        petService.create(userId, petProfileRequest);
        return ResponseEntity.ok(
                CatsgotogedogApiResponse.success("반려동물 프로필 생성 성공", null));
    }

    @PatchMapping("/profile/{petId}")
    public ResponseEntity<CatsgotogedogApiResponse<?>> updateProfile(
            @AuthenticationPrincipal String userId,
            @PathVariable int petId,
            @Valid @ModelAttribute PetProfileRequest petProfileRequest) {
        petService.updateById(userId, petId, petProfileRequest);
        return ResponseEntity.ok(
                CatsgotogedogApiResponse.success("반려동물 프로필 수정 성공", null));
    }

    @DeleteMapping("/profile/{petId}")
    public ResponseEntity<CatsgotogedogApiResponse<?>> deleteProfile(
            @AuthenticationPrincipal String userId,
            @PathVariable int petId) {
        petService.deleteById(userId, petId);
        return ResponseEntity.ok(
                CatsgotogedogApiResponse.success("반려동물 프로필 삭제 성공", null));
    }
}
