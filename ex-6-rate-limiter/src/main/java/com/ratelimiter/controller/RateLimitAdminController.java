package com.ratelimiter.controller;

import com.ratelimiter.dto.ConfigureRateLimitRequest;
import com.ratelimiter.dto.TokenBucketStatus;
import com.ratelimiter.service.TokenBucketRateLimiter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ratelimit")
public class RateLimitAdminController {

    private final TokenBucketRateLimiter rateLimiter;

    public RateLimitAdminController(TokenBucketRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @GetMapping("/status/{clientId}")
    public ResponseEntity<TokenBucketStatus> getStatus(@PathVariable String clientId) {
        return ResponseEntity.ok(rateLimiter.getStatus(clientId));
    }

    @PostMapping("/configure")
    public ResponseEntity<Map<String, Object>> configure(@RequestBody ConfigureRateLimitRequest request) {
        rateLimiter.setClientConfig(request.getClientId(), request.getCapacity(), request.getRefillRate());
        rateLimiter.resetClient(request.getClientId());
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Configured client '" + request.getClientId() + "' with capacity=" + request.getCapacity() + " and refillRate=" + request.getRefillRate() + "/sec"
        ));
    }

    @PostMapping("/reset/{clientId}")
    public ResponseEntity<Map<String, Object>> reset(@PathVariable String clientId) {
        rateLimiter.resetClient(clientId);
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Reset rate limit bucket for client '" + clientId + "'"
        ));
    }

    @GetMapping("/defaults")
    public ResponseEntity<Map<String, Object>> getDefaults() {
        return ResponseEntity.ok(Map.of(
                "defaultCapacity", rateLimiter.getDefaultCapacity(),
                "defaultRefillRate", rateLimiter.getDefaultRefillRate()
        ));
    }
}
