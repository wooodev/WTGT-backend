package com.swyp.catsgotogedog.content.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swyp.catsgotogedog.content.domain.entity.Content;
import com.swyp.catsgotogedog.pet.domain.entity.PetGuide;
import com.swyp.catsgotogedog.pet.domain.response.PetGuideResponse;
import lombok.Builder;

import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
@Builder
public record PlaceDetailResponse(
        int contentId,
        String title,
        String addr1,
        String addr2,
        String image,
        String thumbImage,
        String categoryId,
        int contentTypeId,
        String copyright,
        double mapx,
        double mapy,
        int mlevel,
        String tel,
        int zipcode,
        Double avgScore,
        boolean wishData,
        int wishCnt,
        boolean visited,
        int totalView,
        String overview,
        List<ContentImageResponse> detailImage,
        PetGuide petGuide,
        String restDate,
        Map<String, Object> additionalInformation
) {

    public static PlaceDetailResponse from(
            Content c,
            Double avgScore,
            boolean wishData,
            int wishCnt,
            boolean visited,
            int totalView,
            List<ContentImageResponse> detailImage,
            PetGuide petGuide,
            String restDate,
            Map<String, Object> additionalInformation
    ){

        return PlaceDetailResponse.builder()
                .contentId(c.getContentId())
                .title(c.getTitle())
                .addr1(c.getAddr1())
                .addr2(c.getAddr2())
                .image(c.getImage())
                .thumbImage(c.getThumbImage())
                .categoryId(c.getCategoryId())
                .contentTypeId(c.getContentTypeId())
                .copyright(c.getCopyright())
                .mapx(c.getMapx())
                .mapy(c.getMapy())
                .mlevel(c.getMLevel())
                .tel(c.getTel())
                .zipcode(c.getZipCode())
                .avgScore(avgScore)
                .wishData(wishData)
                .wishCnt(wishCnt)
                .visited(visited)
                .totalView(totalView)
                .overview(c.getOverview())
                .detailImage(detailImage)
                .petGuide(petGuide)
                .restDate(restDate)
                .additionalInformation(additionalInformation)
                .build();
    }
}
