package com.swyp.catsgotogedog.content.domain.response;

import com.swyp.catsgotogedog.content.domain.entity.Content;

public record LastViewHistoryResponse(
        int contentId,
        String title,
        String thumbImage) {

    public static LastViewHistoryResponse from(Content c) {
        return new LastViewHistoryResponse(
                c.getContentId(),
                c.getTitle(),
                c.getThumbImage()
        );
    }
}
