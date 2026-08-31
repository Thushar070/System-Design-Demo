package com.ratelimiter.interceptor;

import com.ratelimiter.dto.TokenBucketStatus;
import com.ratelimiter.service.TokenBucketRateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RateLimitInterceptorTest {

    private TokenBucketRateLimiter rateLimiter;
    private RateLimitInterceptor interceptor;

    @BeforeEach
    void setUp() {
        rateLimiter = mock(TokenBucketRateLimiter.class);
        interceptor = new RateLimitInterceptor(rateLimiter);
    }

    @Test
    void testPreHandleAllowed() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/protected/greeting");
        request.addHeader("X-Client-ID", "test_user");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(rateLimiter.tryConsume("test_user", 1))
                .thenReturn(new TokenBucketStatus(true, 9, 10, "test_user"));

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        assertEquals(200, response.getStatus());
        assertEquals("10", response.getHeader("X-RateLimit-Limit"));
        assertEquals("9", response.getHeader("X-RateLimit-Remaining"));
    }

    @Test
    void testPreHandleRejected() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/protected/greeting");
        request.addHeader("X-Client-ID", "blocked_user");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(rateLimiter.tryConsume("blocked_user", 1))
                .thenReturn(new TokenBucketStatus(false, 0, 10, "blocked_user"));

        boolean result = interceptor.preHandle(request, response, new Object());

        assertFalse(result);
        assertEquals(429, response.getStatus());
        assertEquals("1", response.getHeader("Retry-After"));
        assertTrue(response.getContentAsString().contains("HTTP 429: Too Many Requests"));
    }

    @Test
    void testPreHandleBypassUnprotectedRoute() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/public/health");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean result = interceptor.preHandle(request, response, new Object());

        assertTrue(result);
        verifyNoInteractions(rateLimiter);
    }
}
