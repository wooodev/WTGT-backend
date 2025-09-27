package com.swyp.catsgotogedog.pet.controller;

import com.swyp.catsgotogedog.global.CatsgotogedogApiResponse;
import com.swyp.catsgotogedog.pet.domain.request.PetProfileRequest;
import com.swyp.catsgotogedog.pet.domain.response.PetProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Pet", description = "반려동물 관련 API")
public interface PetControllerSwagger {
    
    @Operation(
        summary = "반려동물 프로필 목록 조회",
        description = "인증된 사용자의 모든 반려동물 프로필 목록을 조회합니다."
    )
    @SecurityRequirement(name = "bearer-key")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "반려동물 프로필 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = PetProfileResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class)))
    })
    ResponseEntity<CatsgotogedogApiResponse<?>> getAllProfiles(
        @Parameter(description = "인증된 사용자 ID", hidden = true)
        String userId
    );

    @Operation(
        summary = "반려동물 프로필 등록",
        description = """
                사용자의 새로운 반려동물 프로필을 등록합니다. 반려동물의 정보와 이미지를 함께 업로드할 수 있습니다. 최대 10마리까지 등록 가능합니다.<br />
                사진을 제외한 모든 정보는 필수로 입력해야 합니다.<br />
                반려동물 크기는 소형, 중형, 대형 중 하나를 선택해야 합니다."""
    )
    @SecurityRequirement(name = "bearer-key")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "반려동물 프로필 생성 성공",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터 또는 반려동물 등록 제한 초과 (최대 10마리)",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class)))
    })
    ResponseEntity<CatsgotogedogApiResponse<?>> createProfile(
        @Parameter(description = "인증된 사용자 ID", hidden = true)
        String userId,
        @Parameter(description = "반려동물 프로필 등록 정보", required = true)
        PetProfileRequest petProfileRequest
    );

    @Operation(
        summary = "반려동물 프로필 수정",
        description = """
                등록된 반려동물의 프로필 정보를 수정합니다. 본인의 반려동물만 수정할 수 있습니다.<br />
                사진을 제외한 모든 정보는 필수로 입력해야 합니다.<br />
                반려동물 크기는 소형, 중형, 대형 중 하나를 선택해야 합니다."""
    )
    @SecurityRequirement(name = "bearer-key")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "반려동물 프로필 수정 성공",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "403", description = "수정 권한이 없음 (본인의 반려동물이 아님)",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "404", description = "반려동물을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class)))
    })
    ResponseEntity<CatsgotogedogApiResponse<?>> updateProfile(
        @Parameter(description = "인증된 사용자 ID", hidden = true)
        String userId,
        @Parameter(description = "수정할 반려동물 ID", required = true)
        int petId,
        @Parameter(description = "반려동물 프로필 수정 정보", required = true)
        PetProfileRequest petProfileRequest
    );

    @Operation(
        summary = "반려동물 프로필 삭제",
        description = "등록된 반려동물의 프로필을 삭제합니다. 본인의 반려동물만 삭제할 수 있습니다."
    )
    @SecurityRequirement(name = "bearer-key")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "반려동물 프로필 삭제 성공",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "403", description = "삭제 권한이 없음 (본인의 반려동물이 아님)",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "404", description = "반려동물을 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class)))
    })
    ResponseEntity<CatsgotogedogApiResponse<?>> deleteProfile(
        @Parameter(description = "인증된 사용자 ID", hidden = true)
        String userId,
        @Parameter(description = "삭제할 반려동물 ID", required = true)
        int petId
    );
}
