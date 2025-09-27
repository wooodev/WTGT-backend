package com.swyp.catsgotogedog.review.domain.entity;

import com.swyp.catsgotogedog.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;

@Entity
@Getter
public class ContentReview extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewId;

    private int userId;

    private int contentId;

    private String content;

    @Column(precision = 2, scale = 1)
    private BigDecimal score;

//    private int like;

}
