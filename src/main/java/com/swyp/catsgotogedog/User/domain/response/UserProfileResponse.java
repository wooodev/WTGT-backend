package com.swyp.catsgotogedog.User.domain.response;

import com.swyp.catsgotogedog.User.domain.entity.User;
import com.swyp.catsgotogedog.pet.domain.response.PetProfileResponse;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class UserProfileResponse {

    private String displayName;
    private String email;
    private String provider;
    private String imageFilename;
    private String imageUrl;
    private List<PetProfileResponse> pets;

    public static UserProfileResponse from(User user) {
        return UserProfileResponse.builder()
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .provider(user.getProvider())
                .imageFilename(user.getImageFilename())
                .imageUrl(user.getImageUrl())
                .pets(convertPets(user))
                .build();
    }

    private static List<PetProfileResponse> convertPets(User user) {
        if (user.getPets() == null) {
            return Collections.emptyList();
        }
        return user.getPets().stream()
                .map(PetProfileResponse::from)
                .collect(Collectors.toList());
    }
}
