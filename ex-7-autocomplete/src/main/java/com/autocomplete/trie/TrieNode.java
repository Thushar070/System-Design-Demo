package com.autocomplete.trie;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {

    private final Map<Character, TrieNode> children = new HashMap<>();
    private boolean isWord = false;
    private long frequency = 0;
    private String word = null;

    public Map<Character, TrieNode> getChildren() {
        return children;
    }

    public boolean isWord() {
        return isWord;
    }

    public void setWord(boolean word) {
        isWord = word;
    }

    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public String getWord() {
        return word;
    }

    public void setWordString(String word) {
        this.word = word;
    }
}
