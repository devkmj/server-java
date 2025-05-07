package kr.hhplus.be.server.lock;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class RedissonLockService {

    private final RedissonClient redissonClient;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 주어진 키 리스트에 대해 락을 획득한 후 task 실행.
     *  - keys.size()==1: 단일 락
     *  - keys.size()>1: key 이름 순으로 정렬한 후 순차 획득 (deadlock 방지)
     */
    public <T> T lock(List<String> keys,
                      int waitTimeSec,
                      int leaseTimeSec,
                      TimeUnit timeUnit,
                      Supplier<T> task) {
        if (keys == null || keys.isEmpty()) {
            throw new IllegalArgumentException("락 키 목록은 비어 있을 수 없습니다.");
        }
        if (keys.size() == 1) {
            return singleLock(keys.get(0), waitTimeSec, leaseTimeSec, timeUnit, task);
        } else {
            // 이름 순 정렬
            List<String> sortedKeys = new ArrayList<>(keys);
            Collections.sort(sortedKeys);
            // RLock 인스턴스 생성 및 순차 획득
            List<RLock> acquired = new ArrayList<>();
            try {
                for (String key : sortedKeys) {
                    RLock lock = redissonClient.getLock(key);
                    boolean obtained = lock.tryLock(waitTimeSec, leaseTimeSec, timeUnit);
                    if (!obtained) {
                        throw new IllegalStateException("멀티락 획득 실패: " + key);
                    }
                    acquired.add(lock);
                }
                // 트랜잭션 완료 시 해제
                registerRelease(acquired);
                return task.get();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                // 부분 해제
                releaseAll(acquired);
                throw new RuntimeException("락 대기 중 인터럽트 발생", e);
            } catch (RuntimeException e) {
                // 부분 해제
                releaseAll(acquired);
                throw e;
            }
        }
    }

    private <T> T singleLock(String key,
                             int waitTimeSec,
                             int leaseTimeSec,
                             TimeUnit timeUnit,
                             Supplier<T> task) {
        RLock lock = redissonClient.getLock(key);
        try {
            boolean obtained = lock.tryLock(waitTimeSec, leaseTimeSec, timeUnit);
            if (!obtained) {
                throw new IllegalStateException("락 획득 실패: " + key);
            }
            // 트랜잭션 완료 시 해제
            registerRelease(Collections.singletonList(lock));
            return task.get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 대기 중 인터럽트 발생", e);
        }
    }

    /**
     * 트랜잭션 완료 후에 전달된 락들을 역순으로 해제
     */
    private void registerRelease(List<RLock> locks) {
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronizationAdapter() {
                    @Override
                    public void afterCompletion(int status) {
                        // 역순 해제
                        ListIterator<RLock> it = locks.listIterator(locks.size());
                        while (it.hasPrevious()) {
                            try {
                                it.previous().unlock();
                            } catch (Exception ex) {
                                logger.warn("락 해제 중 에러: {}", ex.getMessage());
                            }
                        }
                    }
                }
        );
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
