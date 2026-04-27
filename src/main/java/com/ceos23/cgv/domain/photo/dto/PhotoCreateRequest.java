package com.ceos23.cgv.domain.photo.dto;

import com.ceos23.cgv.global.exception.CustomException;
import com.ceos23.cgv.global.exception.ErrorCode;

public record PhotoCreateRequest(
        Long movieId,
        Long personId,
        String name
) {
    //  최소 하나는 존재해야 하며, 둘 다 있는 것도 허용
    public PhotoCreateRequest {
        if (movieId == null && personId == null) {
            throw new CustomException(ErrorCode.PHOTO_TARGET_REQUIRED);
        }
    }
}
