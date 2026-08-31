package com.ratelimiter.service;

import com.ratelimiter.dto.TokenBucketStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenBucketRateLimiter {

    private static final Logger log = LoggerFactory.getLogger(TokenBucketRateLimiter.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisScript<List> tokenBucketScript;

    @Value("${app.rate-limiter.capacity:10}")
    private long defaultCapacity;

    @Value("${app.rate-limiter.refill-rate:2}")
    private long defaultRefillRate;

    private final Map<String, long[]> customClientConfigs = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter(RedisTemplate<String, Object> redisTemplate, RedisScript<List> tokenBucketScript) {
        this.redisTemplate = redisTemplate;
        this.tokenBucketScript = tokenBucketScript;
    }

    public void setClientConfig(String clientId, long capacity, long refillRate) {
        customClientConfigs.put(clientId, new long[]{capacity, refillRate});
    }

    public TokenBucketStatus tryConsume(String clientId, long cost) {
        long capacity = defaultCapacity;
        long refillRate = defaultRefillRate;

        if (customClientConfigs.containsKey(clientId)) {
            long[] cfg = customClientConfigs.get(clientId);
            capacity = cfg[0];
            refillRate = cfg[1];
        }

        String key = "ratelimit:" + clientId;
        long now = Instant.now().getEpochSecond();

        try {
            List<Object> result = redisTemplate.execute(
                    tokenBucketScript,
                    Collections.singletonList(key),
                    String.valueOf(capacity),
                    String.valueOf(refillRate),
                    String.valueOf(cost),
                    String.valueOf(now)
            );

            if (result != null && result.size() >= 3) {
                boolean allowed = ((Number) result.get(0)).intValue() == 1;
                long remaining = ((Number) result.get(1)).longValue();
                long cap = ((Number) result.get(2)).longValue();

                log.info("RateLimiter client={} allowed={} remaining={} cap={}", clientId, allowed, remaining, cap);
                return new TokenBucketStatus(allowed, remaining, cap, clientId);
            }
        } catch (Exception e) {
            log.error("Redis execution failed for client={}; fallback allowing request: {}", clientId, e.getMessage());
            return new TokenBucketStatus(true, defaultCapacity, defaultCapacity, clientId);
        }

        return new TokenBucketStatus(false, 0, capacity, clientId);
    }

    public TokenBucketStatus getStatus(String clientId) {
        String key = "ratelimit:" + clientId;
        long capacity = defaultCapacity;
        long refillRate = defaultRefillRate;

        if (customClientConfigs.containsKey(clientId)) {
            long[] cfg = customClientConfigs.get(clientId);
            capacity = cfg[0];
            refillRate = cfg[1];
        }

        try {
            List<Object> values = redisTemplate.opsForHash().multiGet(key, List.of("tokens", "last_refill"));
            if (values != null && values.size() == 2 && values.get(0) != null && values.get(1) != null) {
                long tokens = Long.parseLong(values.get(0).toString());
                long lastRefill = Long.parseLong(values.get(1).toString());
                long now = Instant.now().getEpochSecond();
                long delta = Math.max(0, now - lastRefill);
                long currentTokens = Math.min(capacity, tokens + delta * refillRate);

                return new TokenBucketStatus(true, currentTokens, capacity, clientId);
            }
        } catch (Exception e) {
            log.warn("Could not fetch status from Redis for client={}: {}", clientId, e.getMessage());
        }

        return new TokenBucketStatus(true, capacity, capacity, clientId);
    }

    public void resetClient(String clientId) {
        String key = "ratelimit:" + clientId;
        redisTemplate.delete(key);
    }

    public long getDefaultCapacity() { return defaultCapacity; }
    public long getDefaultRefillRate() { return defaultRefillRate; }
}
