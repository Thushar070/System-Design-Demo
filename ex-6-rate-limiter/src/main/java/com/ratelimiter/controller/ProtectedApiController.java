package com.ratelimiter.controller;

import com.ratelimiter.dto.RateLimitResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/protected")
public class ProtectedApiController {

    @GetMapping("/greeting")
    public ResponseEntity<RateLimitResponse> getGreeting(HttpServletRequest request) {
        String clientId = request.getHeader("X-Client-ID");
        if (clientId == null || clientId.trim().isEmpty()) {
            clientId = request.getRemoteAddr();
        }

        String remainingStr = responseHeader(request, "X-RateLimit-Remaining");
        long remaining = remainingStr != null ? Long.parseLong(remainingStr) : 0;
        String capStr = responseHeader(request, "X-RateLimit-Limit");
        long cap = capStr != null ? Long.parseLong(capStr) : 10;

        RateLimitResponse response = new RateLimitResponse(
                "ALLOWED",
                "Request successful! Hello from Protected API Endpoint.",
                clientId,
                remaining,
                cap
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/data")
    public ResponseEntity<RateLimitResponse> getData(HttpServletRequest request) {
        String clientId = request.getHeader("X-Client-ID");
        if (clientId == null || clientId.trim().isEmpty()) {
            clientId = request.getRemoteAddr();
        }

        RateLimitResponse response = new RateLimitResponse(
                "ALLOWED",
                "Successfully accessed protected sensitive data payload.",
                clientId,
                10,
                10
        );

        return ResponseEntity.ok(response);
    }

    private String responseHeader(HttpServletRequest request, String name) {
        return request.getHeader(name);
    }
}
