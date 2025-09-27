package com.swyp.catsgotogedog.User.controller;

import com.swyp.catsgotogedog.User.domain.request.UserUpdateRequest;
import com.swyp.catsgotogedog.User.domain.response.UserProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import com.swyp.catsgotogedog.User.domain.response.AccessTokenResponse;
import com.swyp.catsgotogedog.global.CatsgotogedogApiResponse;

@Tag(name = "User", description = "사용자 관련 API")
public interface UserControllerSwagger {

    @Operation(
        summary = "액세스 토큰 재발급",
        description = "리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급받습니다.\n"
			+ "재발급된 토큰은 body를 통해 반환됩니다."
            + " Cookie를 통해 Refresh-Token값을 읽어 재발급을 진행합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"
                    , content = @Content(schema = @Schema(implementation = AccessTokenResponse.class))),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"
                    , content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class)))
    })
    ResponseEntity<?> reIssue(
        @Parameter(description = "리프레시 토큰", hidden = true)
        String refresh
    );

    @Operation(
        summary = "로그아웃",
        description = "사용자 로그아웃을 처리하고 리프레시 토큰을 제거합니다. Cookie를 통해 Refresh-Token값을 읽어 로그아웃 처리를 진행합니다."
    )
    @SecurityRequirement(name = "bearer-key")
    @ApiResponses({
		@ApiResponse(responseCode = "200", description = "로그아웃 성공"
                    , content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "유효하지 않은 토큰"
                    , content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class)))
    })
    ResponseEntity<CatsgotogedogApiResponse<?>> logout(
        @Parameter(description = "리프레시 토큰", hidden = true)
        String refresh
    );
    
    @Operation(
        summary = "사용자 프로필 조회",
        description = "인증된 사용자의 프로필 정보를 조회합니다."
    )
    @SecurityRequirement(name = "bearer-key")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "프로필 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserProfileResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class)))
    })
    ResponseEntity<CatsgotogedogApiResponse<?>> profile(
        @Parameter(description = "인증된 사용자 ID", hidden = true)
        String userId
    );

    @Operation(
        summary = "사용자 정보 수정",
        description = "인증된 사용자의 정보를 수정합니다. 사용자 ID는 인증된 사용자로부터 가져옵니다."
    )
    @SecurityRequirement(name = "bearer-key")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class)))
    })
    ResponseEntity<CatsgotogedogApiResponse<?>> updateProfile(
        @Parameter(description = "인증된 사용자 ID", hidden = true)
        String userId,
        @Parameter(description = "수정할 사용자 정보", required = true)
        UserUpdateRequest request
    );

    @Operation(
        summary = "사용자 프로필 이미지 삭제",
        description = "인증된 사용자의 프로필 이미지를 삭제합니다."
    )
    @SecurityRequirement(name = "bearer-key")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "프로필 이미지 삭제 성공",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class)))
    })
    ResponseEntity<CatsgotogedogApiResponse<?>> deleteProfileImage(
        @Parameter(description = "인증된 사용자 ID", hidden = true)
        String userId
    );

    @Operation(
        summary = "회원 탈퇴",
        description = """
                X-Refresh-Token 쿠키를 제거하고 기존 사용자 정보는 삭제 처리 데이터로 변경됩니다.
                리뷰는 삭제된 유저 정보로 조회됩니다.
            """
    )
    @SecurityRequirement(name = "bearer-key")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공",
            content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 또는 유효하지 않은 토큰",
            content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터",
            content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class))),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
            content = @Content(schema = @Schema(implementation = CatsgotogedogApiResponse.class)))
    })
    ResponseEntity<CatsgotogedogApiResponse<?>> withdraw(
        @Parameter(description = "인증된 사용자 ID", hidden = true)
        String userId,
        @Parameter(description = "리프레시 토큰", hidden = true)
        String refresh
    );
}