package com.ceos23.spring_boot.global.lock;

import com.ceos23.spring_boot.global.exception.BusinessException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.RedissonMultiLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Slf4j
public class RedisLockManager {
    private final RedissonClient redissonClient;

    public <T> T executeWithLock(
            List<String> lockKeys, int waitTime, int leaseTime, TimeUnit timeUnit, Supplier<T> logic) {

        List<RLock> lockList = lockKeys.stream()
                .map(redissonClient::getLock)
                .toList();

        RedissonMultiLock multiLock = new RedissonMultiLock(lockList.toArray(new RLock[0]));
        boolean isLocked = false;

        try {
            isLocked = multiLock.tryLock(waitTime, leaseTime, timeUnit);

            if (!isLocked) {
                log.warn("Lock 획득 실패 (다른 사용자 요청 처리 중입니다) Key: {}", lockKeys);
                throw new BusinessException(ErrorCode.LOCK_ACQUISITION_FAILED);
            }

            return logic.get();

        } catch (InterruptedException e) {
            log.error("Lock 획득 중 인터럽트 발생 Key: {}", lockKeys);
            Thread.currentThread().interrupt();
            throw new BusinessException(ErrorCode.LOCK_INTERRUPTED_ERROR);

        } finally {
            if (isLocked)
                multiLock.unlock();
        }
    }
}
