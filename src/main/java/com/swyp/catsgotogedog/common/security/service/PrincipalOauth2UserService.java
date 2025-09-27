package com.swyp.catsgotogedog.common.security.service;



import com.swyp.catsgotogedog.User.domain.entity.User;
import com.swyp.catsgotogedog.User.repository.UserRepository;
import com.swyp.catsgotogedog.common.oauth2.KakaoUserInfo;
import com.swyp.catsgotogedog.common.oauth2.GoogleUserInfo;
import com.swyp.catsgotogedog.common.oauth2.NaverUserInfo;
import com.swyp.catsgotogedog.common.oauth2.SocialUserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrincipalOauth2UserService extends DefaultOAuth2UserService {

    private static final int RANDDOM_NUMBER_LENGTH = 6;

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest req) {
        OAuth2User oAuth2User = super.loadUser(req);
        String provider = req.getClientRegistration().getRegistrationId();   // google / kakao / naver

        SocialUserInfo info;

        switch (provider) {
            case "kakao" -> {
                KakaoUserInfo kakao = KakaoUserInfo.of(oAuth2User.getAttributes());
                info = new SocialUserInfo(kakao.id(), kakao.email(), kakao.name(), kakao.profile_image());
            }
            case "naver" -> {
                NaverUserInfo naver = NaverUserInfo.of(oAuth2User.getAttributes());
                info = new SocialUserInfo(naver.id(), naver.email(), naver.name(), naver.profileImage());
            }
            case "google" -> {
                GoogleUserInfo google = GoogleUserInfo.of(oAuth2User.getAttributes());
                info = new SocialUserInfo(google.id(), google.email(), google.name(), google.picture());
            }
            default -> throw new IllegalArgumentException("지원하지 않는 소셜 로그인입니다: " + provider);
        }

        User user = userRepository.findByProviderAndProviderId(provider, info.id())
                .orElseGet(() -> {
                    String display_name = info.name();
                    while(userRepository.findByDisplayName(display_name).isPresent()) {
                        display_name = info.name() + generateRandomString();
                    }
                    return userRepository.save(
                        User.builder()
                            .provider(provider)
                            .providerId(info.id())
                            .email(info.email())
                            .displayName(display_name)
                            .imageUrl(info.profileImage())
                            .isActive(Boolean.TRUE)
                            .build()
                    );
                });

        return new PrincipalDetails(user, oAuth2User.getAttributes());
    }

    // 랜덤 숫자(0~9) RANDDOM_NUMBER_LENGTH 값의 자리 만큼 반환 메서드
    private String generateRandomString() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(RANDDOM_NUMBER_LENGTH);
        for (int i = 0; i < RANDDOM_NUMBER_LENGTH; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
