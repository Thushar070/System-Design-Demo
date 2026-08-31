package com.ratelimiter.dto;

public class TokenBucketStatus {
    private boolean allowed;
    private long remainingTokens;
    private long capacity;
    private String clientId;

    public TokenBucketStatus() {}

    public TokenBucketStatus(boolean allowed, long remainingTokens, long capacity, String clientId) {
        this.allowed = allowed;
        this.remainingTokens = remainingTokens;
        this.capacity = capacity;
        this.clientId = clientId;
    }

    public boolean isAllowed() { return allowed; }
    public void setAllowed(boolean allowed) { this.allowed = allowed; }

    public long getRemainingTokens() { return remainingTokens; }
    public void setRemainingTokens(long remainingTokens) { this.remainingTokens = remainingTokens; }

    public long getCapacity() { return capacity; }
    public void setCapacity(long capacity) { this.capacity = capacity; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }
}
