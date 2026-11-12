package com.swyp.catsgotogedog.content.domain.entity;

import com.swyp.catsgotogedog.User.domain.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
public class ViewLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "view_id")
    private Long viewId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_id")
    private Content content;

    @CreatedDate
    @Column(name = "viewed_at",
            nullable = false,
            updatable = false)
    private LocalDateTime viewedAt;
}
