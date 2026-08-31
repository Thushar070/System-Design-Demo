package com.autocomplete.trie;

import com.autocomplete.dto.SuggestionDto;

import java.util.*;

public class Trie {

    private final TrieNode root = new TrieNode();

    public synchronized void insert(String term, long frequency) {
        if (term == null || term.trim().isEmpty()) return;
        String normalized = term.trim().toLowerCase();

        TrieNode curr = root;
        for (char ch : normalized.toCharArray()) {
            curr = curr.getChildren().computeIfAbsent(ch, c -> new TrieNode());
        }

        curr.setWord(true);
        curr.setWordString(normalized);
        curr.setFrequency(frequency);
    }

    public synchronized List<SuggestionDto> searchPrefix(String prefix, int topK) {
        if (prefix == null) return Collections.emptyList();
        String normalized = prefix.trim().toLowerCase();

        TrieNode curr = root;
        for (char ch : normalized.toCharArray()) {
            curr = curr.getChildren().get(ch);
            if (curr == null) {
                return Collections.emptyList(); // Prefix not found
            }
        }

        List<SuggestionDto> candidates = new ArrayList<>();
        collectAllWords(curr, candidates);

        candidates.sort((a, b) -> Long.compare(b.getFrequency(), a.getFrequency()));

        if (topK > 0 && candidates.size() > topK) {
            return candidates.subList(0, topK);
        }

        return candidates;
    }

    private void collectAllWords(TrieNode node, List<SuggestionDto> result) {
        if (node == null) return;
        if (node.isWord()) {
            result.add(new SuggestionDto(node.getWord(), node.getFrequency()));
        }
        for (TrieNode child : node.getChildren().values()) {
            collectAllWords(child, result);
        }
    }

    public synchronized void clear() {
        root.getChildren().clear();
    }
}
