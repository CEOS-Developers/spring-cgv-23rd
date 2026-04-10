package com.ceos23.cgv.domain.photo.dto;

public record PhotoCreateRequest(
        Long movieId,
        Long personId,
        String name
) {
    //  최소 하나는 존재해야 하며, 둘 다 있는 것도 허용
    public PhotoCreateRequest {
        if (movieId == null && personId == null) {
            throw new IllegalArgumentException("사진은 최소한 영화(movieId) 또는 인물(personId) 중 하나 이상의 대상에 등록되어야 합니다.");
        }
    }
}