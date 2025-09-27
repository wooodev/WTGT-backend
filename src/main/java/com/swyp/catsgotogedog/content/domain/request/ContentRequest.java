package com.swyp.catsgotogedog.content.domain.request;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class ContentRequest {
    private int contentId;
    private String categoryId;
    private String addr1;
    private String addr2;
    private String image;
    private String thumbImage;
    private String copyright;
    private double mapx;
    private double mapy;
    private int mlevel;
    private String tel;
    private String title;
    private int zipcode;
    private int contentTypeId;
}
