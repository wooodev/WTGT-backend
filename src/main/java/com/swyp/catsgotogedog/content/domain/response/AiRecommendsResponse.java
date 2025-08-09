package com.swyp.catsgotogedog.content.domain.response;

import com.swyp.catsgotogedog.content.domain.entity.AiRecommends;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AiRecommendsResponse {
    private int contentId;
    private String message;
    private String imageUrl;

    public static AiRecommendsResponse of(AiRecommends aiRecommends) {
        return new AiRecommendsResponse(
                aiRecommends.getContentId(),
                aiRecommends.getMessage(),
                aiRecommends.getImageUrl()
        );
    }
}
