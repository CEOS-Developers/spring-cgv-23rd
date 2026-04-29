package com.ceos.spring_cgv_23rd.domain.reservation.adapter.out.redis;

import com.ceos.spring_cgv_23rd.domain.reservation.application.port.out.SeatHoldPort;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class SeatHoldAdapter implements SeatHoldPort {

    private static final String KEY_PREFIX = "seat:hold:";
    private final RedissonClient redissonClient;

    @Override
    public boolean holdSeats(Long screeningId, List<Long> seatIds, String holderKey, long ttlSeconds) {

        // 좌석 정렬
        List<Long> sortedSeatIds = seatIds.stream().sorted().toList();

        for (int i = 0; i < sortedSeatIds.size(); i++) {
            RBucket<String> bucket = redissonClient.getBucket(buildKey(screeningId, sortedSeatIds.get(i)));
            boolean success = bucket.setIfAbsent(holderKey, Duration.ofSeconds(ttlSeconds));

            if (!success) {
                // 이전에 설정한 키 롤백
                for (int j = 0; j < i; j++) {
                    redissonClient.getBucket(buildKey(screeningId, sortedSeatIds.get(j))).delete();
                }

                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isHeldByUser(Long screeningId, List<Long> seatIds, String holderKey) {
        return seatIds.stream().allMatch(seatId -> {
            RBucket<String> bucket = redissonClient.getBucket(buildKey(screeningId, seatId));
            return holderKey.equals(bucket.get());
        });
    }

    @Override
    public void releaseSeats(Long screeningId, List<Long> seatIds) {
        seatIds.forEach(seatId -> redissonClient.getBucket(buildKey(screeningId, seatId)).delete());
    }


    private String buildKey(Long screeningId, Long seatId) {
        return KEY_PREFIX + screeningId + ":" + seatId;
    }
}
