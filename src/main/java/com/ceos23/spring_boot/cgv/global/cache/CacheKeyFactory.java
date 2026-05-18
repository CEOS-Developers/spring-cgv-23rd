package com.ceos23.spring_boot.cgv.global.cache;

public final class CacheKeyFactory {

    private CacheKeyFactory() {
    }

    public static String screenings(Long movieId, Long cinemaId) {
        return movieId + ":" + cinemaId;
    }
}
