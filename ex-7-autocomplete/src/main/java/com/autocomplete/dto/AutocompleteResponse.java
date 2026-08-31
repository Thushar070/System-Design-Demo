package com.autocomplete.dto;

import java.util.List;

public class AutocompleteResponse {
    private String query;
    private int topK;
    private String cacheSource; // "HIT" or "MISS"
    private long executionTimeMs;
    private List<SuggestionDto> suggestions;

    public AutocompleteResponse() {}

    public AutocompleteResponse(String query, int topK, String cacheSource, long executionTimeMs, List<SuggestionDto> suggestions) {
        this.query = query;
        this.topK = topK;
        this.cacheSource = cacheSource;
        this.executionTimeMs = executionTimeMs;
        this.suggestions = suggestions;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public int getTopK() { return topK; }
    public void setTopK(int topK) { this.topK = topK; }

    public String getCacheSource() { return cacheSource; }
    public void setCacheSource(String cacheSource) { this.cacheSource = cacheSource; }

    public long getExecutionTimeMs() { return executionTimeMs; }
    public void setExecutionTimeMs(long executionTimeMs) { this.executionTimeMs = executionTimeMs; }

    public List<SuggestionDto> getSuggestions() { return suggestions; }
    public void setSuggestions(List<SuggestionDto> suggestions) { this.suggestions = suggestions; }
}
