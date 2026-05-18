package com.ceos23.spring_cgv_23rd.Movie.Service;

import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.MovieSearchResponseDTO;
import com.ceos23.spring_cgv_23rd.Movie.DTO.Response.MovieWrapperDTO;
import com.ceos23.spring_cgv_23rd.Movie.Repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final MovieRepository movieRepository;

    @Async
    public void incrementCount(Long id) {
        String countKey = "movie:count:" + id;
        Long count = redisTemplate.opsForValue().increment(countKey);
        if (count == 1) {
            redisTemplate.expire(countKey, Duration.ofSeconds(60));
        }
    }

    @Scheduled(fixedDelay = 5000)
    public void cachePopularMovies() {
        Set<String> keys = redisTemplate.keys("movie:count:*");
        if (keys == null) return;

        for (String key : keys) {
            Long count = (Long) redisTemplate.opsForValue().get(key);
            if (count != null && count >= 30) {
                Long id = Long.parseLong(key.replace("movie:count:", ""));
                String cacheKey = "movies::" + id;

                // 이미 캐시에 있으면 스킵
                if (redisTemplate.hasKey(cacheKey)) continue;

                movieRepository.findById(id).ifPresent(movie -> {
                    MovieSearchResponseDTO result = MovieSearchResponseDTO.from(MovieWrapperDTO.create(movie));
                    redisTemplate.opsForValue().set(cacheKey, result, Duration.ofMinutes(1));
                    log.info("인기 영화 캐싱 완료: {}", id);
                });
            }
        }
    }
}