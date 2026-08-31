package com.ratelimiter.dto;

public class RateLimitResponse {
    private String status;
    private String message;
    private String clientId;
    private long remainingTokens;
    private long capacity;

    public RateLimitResponse() {}

    public RateLimitResponse(String status, String message, String clientId, long remainingTokens, long capacity) {
        this.status = status;
        this.message = message;
        this.clientId = clientId;
        this.remainingTokens = remainingTokens;
        this.capacity = capacity;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public long getRemainingTokens() { return remainingTokens; }
    public void setRemainingTokens(long remainingTokens) { this.remainingTokens = remainingTokens; }

    public long getCapacity() { return capacity; }
    public void setCapacity(long capacity) { this.capacity = capacity; }
}
