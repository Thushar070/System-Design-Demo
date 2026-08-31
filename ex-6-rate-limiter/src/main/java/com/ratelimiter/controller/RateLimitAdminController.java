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
        double rate = request.getRefillRate();
        rateLimiter.setClientConfig(request.getClientId(), request.getCapacity(), rate);
        rateLimiter.resetClient(request.getClientId());

        String refillDesc = String.format("%.2f tokens/sec", rate);
        if (request.getRefillPeriodSeconds() != null && request.getRefillPeriodSeconds() > 0 && request.getRefillTokens() != null) {
            refillDesc = String.format("%.0f token(s) every %.0f second(s) (%.2f tokens/sec)",
                    request.getRefillTokens(), request.getRefillPeriodSeconds(), rate);
        } else if (rate < 1.0 && rate > 0) {
            refillDesc = String.format("1 token every %.1f seconds (%.2f tokens/sec)", (1.0 / rate), rate);
        }

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Configured client '" + request.getClientId() + "' with capacity=" + request.getCapacity() + " and refillRate=" + refillDesc,
                "capacity", request.getCapacity(),
                "refillRate", rate
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
