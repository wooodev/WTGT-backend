package com.swyp.catsgotogedog.User.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    private int userId;

    private String refreshToken;

    private LocalDateTime expiresAt;

    private Boolean isRevoked;

}
