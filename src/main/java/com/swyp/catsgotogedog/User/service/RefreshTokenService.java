package com.swyp.catsgotogedog.User.service;

import com.swyp.catsgotogedog.User.domain.entity.RefreshToken;
import com.swyp.catsgotogedog.User.domain.entity.User;
import com.swyp.catsgotogedog.User.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {
    private final RefreshTokenRepository repo;

    public RefreshToken save(User user, String token, LocalDateTime expiryMs) {
        repo.deleteByUserId(user.getUserId());
        RefreshToken rt = RefreshToken.builder()
                .userId(user.getUserId())
                .refreshToken(token)
                .expiresAt(expiryMs)
                .isRevoked(Boolean.FALSE)
                .build();
        return repo.save(rt);
    }

    public boolean validate(String token) {
        return repo.findByRefreshToken(token)
                .filter(rt -> rt.getExpiresAt().isAfter(LocalDateTime.now()))
                .isPresent();
    }

    public void delete(String token) {
        repo.findByRefreshToken(token).ifPresent(repo::delete);
    }
}
