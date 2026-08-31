package com.autocomplete.dto;

public class CacheStatsDto {
    private long hits;
    private long misses;
    private double hitRatio;

    public CacheStatsDto() {}

    public CacheStatsDto(long hits, long misses, double hitRatio) {
        this.hits = hits;
        this.misses = misses;
        this.hitRatio = hitRatio;
    }

    public long getHits() { return hits; }
    public void setHits(long hits) { this.hits = hits; }

    public long getMisses() { return misses; }
    public void setMisses(long misses) { this.misses = misses; }

    public double getHitRatio() { return hitRatio; }
    public void setHitRatio(double hitRatio) { this.hitRatio = hitRatio; }
}
