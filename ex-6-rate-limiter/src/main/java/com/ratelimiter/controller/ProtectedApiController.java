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
        String clientId = getClientId(request);
        long remaining = getRemaining(request);
        long capacity = getCapacity(request);

        RateLimitResponse response = new RateLimitResponse(
                "ALLOWED",
                "Request successful! Hello from Protected API Endpoint.",
                clientId,
                remaining,
                capacity
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/data")
    public ResponseEntity<RateLimitResponse> getData(HttpServletRequest request) {
        String clientId = getClientId(request);
        long remaining = getRemaining(request);
        long capacity = getCapacity(request);

        RateLimitResponse response = new RateLimitResponse(
                "ALLOWED",
                "Successfully accessed protected sensitive data payload.",
                clientId,
                remaining,
                capacity
        );

        return ResponseEntity.ok(response);
    }

    private String getClientId(HttpServletRequest request) {
        String clientId = request.getHeader("X-Client-ID");
        if (clientId == null || clientId.trim().isEmpty()) {
            clientId = request.getRemoteAddr();
        }
        return clientId;
    }

    private long getRemaining(HttpServletRequest request) {
        Object attr = request.getAttribute("X-RateLimit-Remaining");
        if (attr instanceof Number) {
            return ((Number) attr).longValue();
        } else if (attr != null) {
            try { return Long.parseLong(attr.toString()); } catch (Exception ignored) {}
        }
        return 0;
    }

    private long getCapacity(HttpServletRequest request) {
        Object attr = request.getAttribute("X-RateLimit-Limit");
        if (attr instanceof Number) {
            return ((Number) attr).longValue();
        } else if (attr != null) {
            try { return Long.parseLong(attr.toString()); } catch (Exception ignored) {}
        }
        return 10;
    }
}
