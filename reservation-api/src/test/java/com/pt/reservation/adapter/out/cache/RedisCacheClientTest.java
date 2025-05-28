package com.pt.reservation.adapter.out.cache;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RedisCacheClientTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private RedisCacheClient redisCacheClient;

    @Test
    public void testSave() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        String key = "user:1";
        Object value = "User test";

        redisCacheClient.save(key, value);

        verify(valueOperations).set(eq(key), eq(value), eq(15L), eq(TimeUnit.MINUTES));
    }

    @Test
    public void testGetSuccess() {
        String name = "User test";
        String key = "user:1";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(name);

        String result = redisCacheClient.get(key, String.class);

        assertEquals(name, result);
        verify(valueOperations).get(key);
    }

    @Test
    public void testGetNotFound() {
        String key = "user:9";

        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(null);

        String result = redisCacheClient.get(key, String.class);

        assertNull(result);
    }

    @Test
    public void testDelete() {
        String key = "user:1";

        redisCacheClient.delete(key);

        verify(redisTemplate, times(1)).delete(key);
    }
}
