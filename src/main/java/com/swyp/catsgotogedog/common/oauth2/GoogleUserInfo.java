package com.swyp.catsgotogedog.common.oauth2;

import java.util.Map;

public record GoogleUserInfo(String id, String email, String name, String picture) {

    public static GoogleUserInfo of(Map<String, Object> attr) {
        return new GoogleUserInfo(
                (String) attr.get("sub"),
                (String) attr.get("email"),
                (String) attr.get("name"),
                (String) attr.get("picture")
        );
    }
}