package kr.hhplus.be.server.lock;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RedissonLockService {

    private final RedissonClient redissonClient;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public <T> T lock(List<String> keys, int waitTimeSec, int leaseTimeSec, TimeUnit timeUnit, Supplier<T> task) {
        validate(keys);
        List<RLock> locks = prepareLocks(keys);
        try {
            acquireAll(locks, waitTimeSec, leaseTimeSec, timeUnit);
            return executeWithProperRelease(locks, task);
        } catch(InterruptedException e){
            Thread.currentThread().interrupt();
            logger.warn(e.getMessage(), e);
            releaseAll(locks);
            throw new RuntimeException(e);
        } catch (RuntimeException ex){
            releaseAll(locks);
            throw ex;
        }
    }

    private <T> T executeWithProperRelease(List<RLock> locks, Supplier<T> task) {
        boolean txActive = TransactionSynchronizationManager.isSynchronizationActive();
        if (txActive) {
            registerAfterCompletion(locks);
            return task.get();
        }
        try {
            return task.get();
        } finally {
            releaseAll(locks);
        }
    }

    private void registerAfterCompletion(List<RLock> locks) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                releaseAllReversed(locks);
            }
        });
    }

    private void releaseAllReversed(List<RLock> locks) {
        for (int i = locks.size() - 1; i >= 0; i--) {
            RLock lock = locks.get(i);
            try {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            } catch (Exception e) {
                logger.warn("락 해제 실패 ({}): {}", lock.getName(), e.getMessage());
            }
        }
    }

    private void acquireAll(List<RLock> locks,
                           int waitTimeSec,
                           int leaseTimeSec,
                           TimeUnit timeUnit) throws InterruptedException {
        for (RLock lock : locks) {
            boolean acquired = lock.tryLock(waitTimeSec, leaseTimeSec, timeUnit);
            if (!acquired) {
                throw new IllegalStateException("락 획득 실패: " + lock.getName());
            }
        }
    }

    private List<RLock> prepareLocks(List<String> keys) {
        if(keys.size() > 1) {
            return keys.stream()
                    .sorted()
                    .map(redissonClient::getLock)
                    .collect(Collectors.toList());
        }
        return Collections.singletonList(redissonClient.getLock(keys.get(0)));
    }

    private void validate(List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            throw new IllegalArgumentException("락 키 목록은 비어 있을 수 없습니다.");
        }
    }

    /**
     * 즉시 해제 (예외 상황에서 부분 해제)
     */
    private void releaseAll(List<RLock> locks) {
        // 획득 순서 역순으로 해제
        Collections.reverse(locks);
        for (RLock l : locks) {
            try {
                if (l.isHeldByCurrentThread()) {
                    l.unlock();
                }
            } catch (Exception ex) {
                logger.warn("부분 해제 중 에러: {}", ex.getMessage());
            }
        }
    }

}
