package com.swyp.catsgotogedog.content.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
public class ViewTotal {
    @Id
    @Column(name = "content_id")
    private int contentId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_id")
    private Content content;

    @Column(name = "total_view", nullable = false)
    private int totalView;

    @LastModifiedDate
    @Column(name = "updated_at",
            nullable = false)
    private LocalDateTime updatedAt;

}
