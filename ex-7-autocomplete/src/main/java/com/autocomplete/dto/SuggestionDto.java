package com.autocomplete.dto;

import java.io.Serializable;
import java.util.Objects;

public class SuggestionDto implements Serializable {
    private String term;
    private long frequency;

    public SuggestionDto() {}

    public SuggestionDto(String term, long frequency) {
        this.term = term;
        this.frequency = frequency;
    }

    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }

    public long getFrequency() { return frequency; }
    public void setFrequency(long frequency) { this.frequency = frequency; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SuggestionDto that = (SuggestionDto) o;
        return frequency == that.frequency && Objects.equals(term, that.term);
    }

    @Override
    public int hashCode() {
        return Objects.hash(term, frequency);
    }
}
