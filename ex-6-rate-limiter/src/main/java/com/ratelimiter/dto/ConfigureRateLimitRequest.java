package com.ratelimiter.dto;

public class ConfigureRateLimitRequest {
    private String clientId;
    private long capacity;
    private long refillRate;

    public ConfigureRateLimitRequest() {}

    public ConfigureRateLimitRequest(String clientId, long capacity, long refillRate) {
        this.clientId = clientId;
        this.capacity = capacity;
        this.refillRate = refillRate;
    }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public long getCapacity() { return capacity; }
    public void setCapacity(long capacity) { this.capacity = capacity; }

    public long getRefillRate() { return refillRate; }
    public void setRefillRate(long refillRate) { this.refillRate = refillRate; }
}
