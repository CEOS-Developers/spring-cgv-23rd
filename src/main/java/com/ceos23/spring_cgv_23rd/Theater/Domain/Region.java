package com.ceos23.spring_cgv_23rd.Theater.Domain;

import com.ceos23.spring_cgv_23rd.global.Exception.CustomException;
import com.ceos23.spring_cgv_23rd.global.Exception.ErrorCode;
import lombok.Getter;

public enum Region {
    SEOUL("서울"),
    GYEONGGI("경기"),
    INCHEON("인천"),
    GANGWON("강원"),
    CHUNGCHEONG("대전/충청"),
    DAEGU("대구"),
    BUSAN_ULSAN("부산/울산"),
    GYEONGSANG("경상"),
    HONAM_JEJU("광주/전라/제주");

    @Getter
    private String regionName;

    Region(String name) {
        this.regionName = name;
    }

    public static Region findRegion(String address) {
        String addressRegion = address.split(" ")[0];

        if(addressRegion.startsWith("서울")){
            return Region.SEOUL;
        } else if (addressRegion.startsWith("경기")){
            return Region.GYEONGGI;
        } else if (addressRegion.startsWith("강원")){
            return Region.GANGWON;
        } else if (addressRegion.startsWith("대전") || addressRegion.startsWith("충청")){
            return Region.CHUNGCHEONG;
        } else if (addressRegion.startsWith("대구")){
            return Region.DAEGU;
        } else if (addressRegion.startsWith("부산") || addressRegion.startsWith("울산")){
            return Region.BUSAN_ULSAN;
        } else if (addressRegion.startsWith("경상")){
            return Region.GYEONGSANG;
        } else if (addressRegion.startsWith("광주") || addressRegion.startsWith("전라") || addressRegion.startsWith("제주")){
            return Region.HONAM_JEJU;
        } else {
            throw new CustomException(ErrorCode.NOT_FOUND_REGION);
        }
    }
}
