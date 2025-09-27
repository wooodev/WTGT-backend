package com.swyp.catsgotogedog.content.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ai_recommends")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiRecommends {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recommends_id")
    private Integer recommendsId;

    @Column(name = "content_id", nullable = false)
    private Integer contentId;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "image_url")
    private String imageUrl;
}
