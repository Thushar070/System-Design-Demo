package com.consistenthashing.dto;

public class CreateNodeRequest {
    private String host;
    private int port;

    public CreateNodeRequest() {}

    public CreateNodeRequest(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }
}
