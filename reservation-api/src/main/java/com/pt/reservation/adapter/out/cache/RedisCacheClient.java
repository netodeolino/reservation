package com.pt.reservation.adapter.out.cache;

import com.pt.reservation.application.ports.out.cache.CachePort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisCacheClient implements CachePort {
    private final RedisTemplate<String, Object> redisTemplate;

    private static final long DEFAULT_TIMEOUT_VALUE = 15;
    private static final TimeUnit CACHE_TIMEOUT_UNIT = TimeUnit.MINUTES;

    public RedisCacheClient(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void save(String key, Object value) {
        redisTemplate.opsForValue().set(key, value, DEFAULT_TIMEOUT_VALUE, CACHE_TIMEOUT_UNIT);
    }

    @Override
    public <T> T get(String key, Class<T> type) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? type.cast(value) : null;
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
