package com.swyp.catsgotogedog.common.oauth2;

import java.util.Map;


public record KakaoUserInfo(String id, String email, String name, String profile_image) {
    public static KakaoUserInfo of(Map<String, Object> attr) {
        String id = String.valueOf(attr.get("id"));

        Map<String, Object> kakaoAccount = (Map<String, Object>) attr.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String nickname = (String) profile.get("nickname");
        String profileImage = (String) profile.get("profile_image_url");
        String email = (String) kakaoAccount.get("email");

        System.out.println("emial : "+email);

        return new KakaoUserInfo(id, email, nickname, profileImage);
    }
}
