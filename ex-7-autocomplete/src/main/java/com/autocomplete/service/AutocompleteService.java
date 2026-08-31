package com.autocomplete.service;

import com.autocomplete.dto.AutocompleteResponse;
import com.autocomplete.dto.CacheStatsDto;
import com.autocomplete.dto.SuggestionDto;
import com.autocomplete.trie.Trie;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AutocompleteService {

    private static final Logger log = LoggerFactory.getLogger(AutocompleteService.class);

    private final Trie trie = new Trie();
    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.autocomplete.default-k:5}")
    private int defaultK;

    @Value("${app.autocomplete.cache-ttl-minutes:10}")
    private long cacheTtlMinutes;

    private final AtomicLong cacheHits = new AtomicLong(0);
    private final AtomicLong cacheMisses = new AtomicLong(0);

    public AutocompleteService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addSearchTerm(String term, long frequency) {
        trie.insert(term, frequency);
        log.info("Inserted term='{}' frequency={}", term, frequency);
    }

    public AutocompleteResponse search(String prefix, Integer topKParam) {
        long startTime = System.nanoTime();

        if (prefix == null || prefix.trim().isEmpty()) {
            return new AutocompleteResponse("", 0, "MISS", 0, Collections.emptyList());
        }

        String normalizedPrefix = prefix.trim().toLowerCase();
        int k = (topKParam != null && topKParam > 0) ? topKParam : defaultK;
        String cacheKey = "autocomplete:" + normalizedPrefix + ":" + k;

        // 1. Try Redis Cache
        try {
            String cachedJson = redisTemplate.opsForValue().get(cacheKey);
            if (cachedJson != null) {
                List<SuggestionDto> suggestions = objectMapper.readValue(cachedJson, new TypeReference<List<SuggestionDto>>() {});
                cacheHits.incrementAndGet();
                long elapsed = (System.nanoTime() - startTime) / 1_000_000;
                log.info("CACHE HIT for prefix='{}' ({} ms)", normalizedPrefix, elapsed);
                return new AutocompleteResponse(normalizedPrefix, k, "HIT", elapsed, suggestions);
            }
        } catch (Exception e) {
            log.warn("Redis read error for key {}: {}", cacheKey, e.getMessage());
        }

        // 2. Cache Miss -> Search Trie
        cacheMisses.incrementAndGet();
        List<SuggestionDto> suggestions = trie.searchPrefix(normalizedPrefix, k);
        long elapsed = (System.nanoTime() - startTime) / 1_000_000;
        log.info("CACHE MISS for prefix='{}' -> Trie returned {} suggestions ({} ms)", normalizedPrefix, suggestions.size(), elapsed);

        // 3. Cache Result in Redis
        try {
            String json = objectMapper.writeValueAsString(suggestions);
            redisTemplate.opsForValue().set(cacheKey, json, Duration.ofMinutes(cacheTtlMinutes));
        } catch (Exception e) {
            log.warn("Redis write error for key {}: {}", cacheKey, e.getMessage());
        }

        return new AutocompleteResponse(normalizedPrefix, k, "MISS", elapsed, suggestions);
    }

    public CacheStatsDto getCacheStats() {
        long hits = cacheHits.get();
        long misses = cacheMisses.get();
        long total = hits + misses;
        double ratio = total > 0 ? (double) hits / total : 0.0;
        return new CacheStatsDto(hits, misses, ratio);
    }

    public void clearCache() {
        try {
            Set<String> keys = redisTemplate.keys("autocomplete:*");
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.info("Cleared {} keys from Redis autocomplete cache", keys.size());
            }
        } catch (Exception e) {
            log.warn("Failed to clear Redis cache: {}", e.getMessage());
        }
    }

    public Trie getTrie() { return trie; }
}
