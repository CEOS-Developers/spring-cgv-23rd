package com.ceos23.cgv_clone.favorite.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FavoriteResponse {
    private boolean isFavorite;

    public static FavoriteResponse of(boolean isFavorite) {
        return new FavoriteResponse(isFavorite);
    }
}
