package com.settler.infra.ratelimit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisRateLimiter {

    private final StringRedisTemplate redis;

    public RedisRateLimiter(StringRedisTemplate redis) {
        this.redis = redis;
    }

    /**
     * Atomic increment and return current counter. Expire set to windowSeconds on first set.
     */
    public long incrementAndGet(String key, long windowSeconds) {
        Long val = redis.opsForValue().increment(key);
        if (val != null && val == 1L) {
            redis.expire(key, Duration.ofSeconds(windowSeconds));
        }
        return val != null ? val : 0;
    }

    public void reset(String key) {
        redis.delete(key);
    }
}
