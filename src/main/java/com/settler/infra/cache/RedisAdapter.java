package com.settler.infra.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
public class RedisAdapter {

    private static final Logger log = LoggerFactory.getLogger(RedisAdapter.class);
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisAdapter(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public <T> void put(String key, T value, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl);
        } catch (Exception e) {
            log.warn("Redis put failed for key {}", key, e);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            return Optional.ofNullable((T) value);
        } catch (Exception e) {
            log.warn("Redis get failed for key {}", key, e);
            return Optional.empty();
        }
    }

    public void evict(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("Redis evict failed for key {}", key, e);
        }
    }
}
