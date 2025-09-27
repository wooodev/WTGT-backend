package com.swyp.catsgotogedog.common.oauth2;

import java.util.Map;

public record NaverUserInfo(String id, String email, String name, String profileImage) {

    public static NaverUserInfo of(Map<String, Object> attr) {
        Map<String, Object> res = (Map<String, Object>) attr.get("response");

        return new NaverUserInfo(
                (String) res.get("id"),
                (String) res.get("email"),
                (String) res.get("name"),
                (String) res.get("profile_image")
        );
    }
}
