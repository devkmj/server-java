package kr.hhplus.be.server.lock;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
public class RedissonLockService {

    private final RedissonClient redissonClient;

    public <T> T lock(String key, int waitTimeSec, int leaseTimeSec, TimeUnit timeUnit, Supplier<T> task) {
        RLock lock = redissonClient.getLock(key);
        boolean isLocked = false;
        try{
            isLocked = lock.tryLock(waitTimeSec, leaseTimeSec, timeUnit);
            if(!isLocked) {
                throw new IllegalStateException("락 획득 실패 : " + key);
            }
            return task.get();
        } catch (Exception e){
            Thread.currentThread().interrupt();
            throw new RuntimeException("락 대기 중 인터럽트 발생", e);
        } finally {
            if(isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
