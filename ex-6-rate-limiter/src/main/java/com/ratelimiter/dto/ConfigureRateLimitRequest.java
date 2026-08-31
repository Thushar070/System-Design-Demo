package com.ratelimiter.dto;

public class ConfigureRateLimitRequest {
    private String clientId;
    private long capacity;
    private double refillRate;
    private Double refillTokens;
    private Double refillPeriodSeconds;

    public ConfigureRateLimitRequest() {}

    public ConfigureRateLimitRequest(String clientId, long capacity, double refillRate) {
        this.clientId = clientId;
        this.capacity = capacity;
        this.refillRate = refillRate;
    }

    public ConfigureRateLimitRequest(String clientId, long capacity, double refillTokens, double refillPeriodSeconds) {
        this.clientId = clientId;
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillPeriodSeconds = refillPeriodSeconds;
        this.refillRate = refillPeriodSeconds > 0 ? refillTokens / refillPeriodSeconds : refillTokens;
    }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public long getCapacity() { return capacity; }
    public void setCapacity(long capacity) { this.capacity = capacity; }

    public double getRefillRate() {
        if (refillPeriodSeconds != null && refillPeriodSeconds > 0 && refillTokens != null) {
            return refillTokens / refillPeriodSeconds;
        }
        return refillRate;
    }
    public void setRefillRate(double refillRate) { this.refillRate = refillRate; }

    public Double getRefillTokens() { return refillTokens; }
    public void setRefillTokens(Double refillTokens) { this.refillTokens = refillTokens; }

    public Double getRefillPeriodSeconds() { return refillPeriodSeconds; }
    public void setRefillPeriodSeconds(Double refillPeriodSeconds) { this.refillPeriodSeconds = refillPeriodSeconds; }
}
