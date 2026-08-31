package com.autocomplete.service;

import com.autocomplete.dto.AutocompleteResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AutocompleteServiceTest {

    private StringRedisTemplate redisTemplate;
    private ValueOperations<String, String> valueOperations;
    private AutocompleteService service;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(StringRedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        service = new AutocompleteService(redisTemplate);
        service.addSearchTerm("google", 90000);
        service.addSearchTerm("google maps", 45000);
        service.addSearchTerm("github", 40000);
    }

    @Test
    void testCacheMissExecutesTrieSearch() {
        when(valueOperations.get(anyString())).thenReturn(null);

        AutocompleteResponse res = service.search("goog", 2);

        assertNotNull(res);
        assertEquals("MISS", res.getCacheSource());
        assertEquals(2, res.getSuggestions().size());
        assertEquals("google", res.getSuggestions().get(0).getTerm());

        verify(valueOperations, times(1)).set(eq("autocomplete:goog:2"), anyString(), any());
    }

    @Test
    void testCacheHitReturnsCachedSuggestions() {
        String json = "[{\"term\":\"google\",\"frequency\":90000},{\"term\":\"google maps\",\"frequency\":45000}]";
        when(valueOperations.get("autocomplete:goog:2")).thenReturn(json);

        AutocompleteResponse res = service.search("goog", 2);

        assertNotNull(res);
        assertEquals("HIT", res.getCacheSource());
        assertEquals(2, res.getSuggestions().size());
        assertEquals("google", res.getSuggestions().get(0).getTerm());
    }
}
