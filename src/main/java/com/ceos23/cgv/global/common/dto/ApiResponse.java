package com.ceos23.cgv.global.common.dto;

import org.springframework.http.HttpStatus;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ApiResponse<T>(
        int status,
        String message,
        T data
) {
    // 1. 단일 데이터 응답용
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), "요청에 성공하였습니다.", data);
    }

    // 2. Entity 리스트를 DTO 리스트로 변환하여 응답하는 헬퍼 메서드!
    public static <T, R> ApiResponse<List<R>> success(List<T> entities, Function<T, R> mapper) {
        List<R> dtoList = entities.stream()
                .map(mapper)
                .collect(Collectors.toList());
        return new ApiResponse<>(HttpStatus.OK.value(), "요청에 성공하였습니다.", dtoList);
    }

    // 3. POST 생성 성공용
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(HttpStatus.CREATED.value(), "생성이 완료되었습니다.", data);
    }
}