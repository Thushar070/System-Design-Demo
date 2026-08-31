package com.ratelimiter.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ratelimiter.dto.RateLimitResponse;
import com.ratelimiter.dto.TokenBucketStatus;
import com.ratelimiter.service.TokenBucketRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final TokenBucketRateLimiter rateLimiter;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RateLimitInterceptor(TokenBucketRateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // Only enforce rate limits on protected API routes
        if (!uri.startsWith("/api/protected") && !uri.startsWith("/api/limited")) {
            return true;
        }

        String clientId = request.getHeader("X-Client-ID");
        if (clientId == null || clientId.trim().isEmpty()) {
            clientId = request.getRemoteAddr();
            if (clientId == null || clientId.equals("0:0:0:0:0:0:0:1")) {
                clientId = "127.0.0.1";
            }
        }

        TokenBucketStatus status = rateLimiter.tryConsume(clientId, 1);

        response.setHeader("X-RateLimit-Limit", String.valueOf(status.getCapacity()));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(status.getRemainingTokens()));

        if (!status.isAllowed()) {
            response.setStatus(429); // HTTP 429 Too Many Requests
            response.setHeader("Retry-After", "1");
            response.setContentType("application/json");

            RateLimitResponse res = new RateLimitResponse(
                    "REJECTED",
                    "HTTP 429: Too Many Requests. Rate limit exceeded for client '" + clientId + "'.",
                    clientId,
                    status.getRemainingTokens(),
                    status.getCapacity()
            );

            response.getWriter().write(objectMapper.writeValueAsString(res));
            return false;
        }

        return true;
    }
}
