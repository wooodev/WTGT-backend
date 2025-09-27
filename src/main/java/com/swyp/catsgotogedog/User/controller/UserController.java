package com.swyp.catsgotogedog.User.controller;

import com.swyp.catsgotogedog.User.domain.entity.User;
import com.swyp.catsgotogedog.User.domain.request.UserUpdateRequest;
import com.swyp.catsgotogedog.User.domain.response.AccessTokenResponse;
import com.swyp.catsgotogedog.User.domain.response.UserProfileResponse;
import com.swyp.catsgotogedog.User.repository.UserRepository;
import com.swyp.catsgotogedog.User.service.RefreshTokenService;
import com.swyp.catsgotogedog.User.service.UserService;
import com.swyp.catsgotogedog.common.util.JwtTokenUtil;
import com.swyp.catsgotogedog.global.CatsgotogedogApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Slf4j
public class UserController implements UserControllerSwagger {

    private final JwtTokenUtil jwt;
    private final RefreshTokenService rtService;
    private final UserService userService;
    private final UserRepository userRepo;

    @PostMapping("/reissue")
    public ResponseEntity<CatsgotogedogApiResponse<?>> reIssue(
            @CookieValue(value = "X-Refresh-Token", required = false) String refresh) {
        return ResponseEntity.ok(CatsgotogedogApiResponse.success("재발급 성공",
            new AccessTokenResponse(userService.reIssue(refresh))));
    }

    @PostMapping("/logout")
    public ResponseEntity<CatsgotogedogApiResponse<?>> logout(
            @CookieValue("X-Refresh-Token") String refresh) {

        userService.logout(refresh);
        ResponseCookie cookie = ResponseCookie.from(("X-Refresh-Token"), refresh)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .sameSite("None")
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(CatsgotogedogApiResponse.success("로그아웃 성공", null));
    }

    @GetMapping("/profile")
    public ResponseEntity<CatsgotogedogApiResponse<?>> profile(
            @AuthenticationPrincipal String userId) {
        User user = userService.profile(userId);
        return ResponseEntity.ok(
                CatsgotogedogApiResponse.success("사용자 프로필 조회 성공", UserProfileResponse.from(user)));
    }

    @PatchMapping("/profile")
    public ResponseEntity<CatsgotogedogApiResponse<?>> updateProfile(
            @AuthenticationPrincipal String userId,
            @Valid @ModelAttribute UserUpdateRequest request) {
        userService.update(userId, request);
        return ResponseEntity.ok(
                CatsgotogedogApiResponse.success("프로필 수정 성공", null));
    }

    @DeleteMapping("/profile/image")
    public ResponseEntity<CatsgotogedogApiResponse<?>> deleteProfileImage(
            @AuthenticationPrincipal String userId) {
        userService.deleteProfileImage(userId);
        return ResponseEntity.ok(
                CatsgotogedogApiResponse.success("프로필 이미지 삭제 성공", null));
    }

    //todo :: 소셜 연결 해제 구현 필요 (현재 DB를 통한 삭제만 구현)
    @DeleteMapping("/withdraw")
    public ResponseEntity<CatsgotogedogApiResponse<?>> withdraw(
        @AuthenticationPrincipal String userId,
        @CookieValue("X-Refresh-Token") String refresh) {

        userService.withdraw(userId, refresh);

        ResponseCookie cookie = ResponseCookie.from(("X-Refresh-Token"), refresh)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(0)
            .sameSite("None")
            .build();

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(CatsgotogedogApiResponse.success("회원 탈퇴 성공", null));
    }
}