package com.swyp.catsgotogedog.common.util;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expire-min}")
    private long accessMin;

    @Value("${jwt.refresh-expire-day}")
    private int refreshDay;

    private Key key;

    @PostConstruct
    private void init() {
        key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }


    public String createAccessToken(String sub, String email, String displayName) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(sub)
                .claim("email", email)
                .claim("displayName", displayName)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessMin * 60_000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(String sub, String email, String displayName) {
        Date now = new Date();
        long refreshMs = Duration.ofDays(refreshDay).toMillis();
        return Jwts.builder()
                .setSubject(sub)
                .claim("email", email)
                .claim("displayName", displayName)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getSubject(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token).getBody().getSubject();
    }

    public String getEmail(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token).getBody().get("email", String.class);
    }

    public String getDisplayName(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
            .parseClaimsJws(token).getBody().get("displayName", String.class);
    }

    public LocalDateTime getRefreshTokenExpiry() {
        return LocalDateTime.now().plusDays(refreshDay);
    }
}
