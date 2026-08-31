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

    @Value("${app.rate-limiter.refill-rate:2.0}")
    private double defaultRefillRate;

    private final Map<String, double[]> customClientConfigs = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter(RedisTemplate<String, Object> redisTemplate, RedisScript<List> tokenBucketScript) {
        this.redisTemplate = redisTemplate;
        this.tokenBucketScript = tokenBucketScript;
    }

    public void setClientConfig(String clientId, long capacity, double refillRate) {
        customClientConfigs.put(clientId, new double[]{capacity, refillRate});
    }

    public void setClientConfig(String clientId, long capacity, double refillTokens, double refillPeriodSeconds) {
        double rate = refillPeriodSeconds > 0 ? refillTokens / refillPeriodSeconds : refillTokens;
        setClientConfig(clientId, capacity, rate);
    }

    public TokenBucketStatus tryConsume(String clientId, long cost) {
        long capacity = defaultCapacity;
        double refillRate = defaultRefillRate;

        if (customClientConfigs.containsKey(clientId)) {
            double[] cfg = customClientConfigs.get(clientId);
            capacity = (long) cfg[0];
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

                log.info("RateLimiter client={} allowed={} remaining={} cap={} refillRate={}/s", clientId, allowed, remaining, cap, refillRate);
                return new TokenBucketStatus(allowed, remaining, cap, refillRate, clientId);
            }
        } catch (Exception e) {
            log.error("Redis execution failed for client={}; fallback allowing request: {}", clientId, e.getMessage());
            return new TokenBucketStatus(true, defaultCapacity, defaultCapacity, defaultRefillRate, clientId);
        }

        return new TokenBucketStatus(false, 0, capacity, refillRate, clientId);
    }

    public TokenBucketStatus getStatus(String clientId) {
        String key = "ratelimit:" + clientId;
        long capacity = defaultCapacity;
        double refillRate = defaultRefillRate;

        if (customClientConfigs.containsKey(clientId)) {
            double[] cfg = customClientConfigs.get(clientId);
            capacity = (long) cfg[0];
            refillRate = cfg[1];
        }

        try {
            Object tokenObj = redisTemplate.opsForHash().get(key, "tokens");
            Object refillObj = redisTemplate.opsForHash().get(key, "last_refill");
            if (tokenObj != null && refillObj != null) {
                double tokens = Double.parseDouble(tokenObj.toString());
                double lastRefill = Double.parseDouble(refillObj.toString());
                long now = Instant.now().getEpochSecond();
                double delta = Math.max(0, now - lastRefill);
                double exactTokens = Math.min(capacity, tokens + delta * refillRate);
                long remaining = (long) Math.floor(exactTokens);

                return new TokenBucketStatus(true, remaining, exactTokens, capacity, refillRate, clientId);
            }
        } catch (Exception e) {
            log.warn("Could not fetch status from Redis for client={}: {}", clientId, e.getMessage());
        }

        return new TokenBucketStatus(true, capacity, capacity, refillRate, clientId);
    }

    public void resetClient(String clientId) {
        String key = "ratelimit:" + clientId;
        redisTemplate.delete(key);
    }

    public long getDefaultCapacity() { return defaultCapacity; }
    public double getDefaultRefillRate() { return defaultRefillRate; }
}
