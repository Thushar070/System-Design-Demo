package com.autocomplete.trie;

import com.autocomplete.dto.SuggestionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrieTest {

    private Trie trie;

    @BeforeEach
    void setUp() {
        trie = new Trie();
        trie.insert("apple", 15000);
        trie.insert("application", 12000);
        trie.insert("app store", 9500);
        trie.insert("appointment", 8000);
        trie.insert("amazon", 50000);
    }

    @Test
    void testPrefixMatchingAndTopKSorting() {
        List<SuggestionDto> suggestions = trie.searchPrefix("app", 3);

        assertNotNull(suggestions);
        assertEquals(3, suggestions.size());
        assertEquals("apple", suggestions.get(0).getTerm());
        assertEquals(15000, suggestions.get(0).getFrequency());
        assertEquals("application", suggestions.get(1).getTerm());
        assertEquals("app store", suggestions.get(2).getTerm());
    }

    @Test
    void testNonExistentPrefix() {
        List<SuggestionDto> suggestions = trie.searchPrefix("xyz", 5);
        assertNotNull(suggestions);
        assertTrue(suggestions.isEmpty());
    }

    @Test
    void testCaseInsensitivity() {
        List<SuggestionDto> suggestions = trie.searchPrefix("APP", 2);
        assertEquals(2, suggestions.size());
        assertEquals("apple", suggestions.get(0).getTerm());
    }
}
