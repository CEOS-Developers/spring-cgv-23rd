package com.ceos23.cgv.domain.user.dto;

import com.ceos23.cgv.domain.user.entity.Cinetalk;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CinetalkResponse {
    private Long cinetalkId;
    private String authorName;
    private String title;
    private String content;
    private int likeCount;
    private String movieTitle; // 없을 수도 있음
    private String cinemaName; // 없을 수도 있음

    public static CinetalkResponse from(Cinetalk cinetalk) {
        return CinetalkResponse.builder()
                .cinetalkId(cinetalk.getId())
                .authorName(cinetalk.getUser().getNickname())
                .title(cinetalk.getTitle())
                .content(cinetalk.getContent())
                .likeCount(cinetalk.getLikeCount())
                // NullPointerException 방지를 위해 영화와 극장이 있을 때만 이름을 가져옴
                .movieTitle(cinetalk.getMovie() != null ? cinetalk.getMovie().getTitle() : null)
                .cinemaName(cinetalk.getCinema() != null ? cinetalk.getCinema().getName() : null)
                .build();
    }
}