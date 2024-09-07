package com.startingblue.fourtooncookie.diary.dto.request;

import lombok.Getter;

@Getter
public class DiaryFavoriteRequest {
    private boolean isFavorite;

    public boolean isFavorite() {
        return isFavorite;
    }
}
