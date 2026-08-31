package com.ratelimiter.service;

import com.ratelimiter.dto.TokenBucketStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TokenBucketRateLimiterTest {

    private RedisTemplate<String, Object> redisTemplate;
    private RedisScript<List> script;
    private TokenBucketRateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(RedisTemplate.class);
        script = mock(RedisScript.class);
        rateLimiter = new TokenBucketRateLimiter(redisTemplate, script);
    }

    @Test
    void testTryConsumeAllowed() {
        when(redisTemplate.execute(eq(script), anyList(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(List.of(1, 9L, 10L));

        TokenBucketStatus status = rateLimiter.tryConsume("client_test", 1);

        assertNotNull(status);
        assertTrue(status.isAllowed());
        assertEquals(9, status.getRemainingTokens());
        assertEquals(10, status.getCapacity());
        assertEquals("client_test", status.getClientId());
    }

    @Test
    void testTryConsumeRejected() {
        when(redisTemplate.execute(eq(script), anyList(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(List.of(0, 0L, 10L));

        TokenBucketStatus status = rateLimiter.tryConsume("client_test", 1);

        assertNotNull(status);
        assertFalse(status.isAllowed());
        assertEquals(0, status.getRemainingTokens());
        assertEquals(10, status.getCapacity());
    }

    @Test
    void testCustomClientConfig() {
        rateLimiter.setClientConfig("vip_client", 50, 10.0);
        when(redisTemplate.execute(eq(script), anyList(), eq("50"), anyString(), anyString(), anyString()))
                .thenReturn(List.of(1, 49L, 50L));

        TokenBucketStatus status = rateLimiter.tryConsume("vip_client", 1);
        assertTrue(status.isAllowed());
        assertEquals(49, status.getRemainingTokens());
        assertEquals(50, status.getCapacity());
    }
}
