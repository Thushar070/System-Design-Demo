package com.consistenthashing.dto;

import java.util.Map;

public class DistributionReport {
    private Map<String, Long> counts;
    private Map<String, Integer> vnodes;
    private long total;
    private Map<String, Long> delta;

    public DistributionReport() {}

    public DistributionReport(Map<String, Long> counts, Map<String, Integer> vnodes, long total, Map<String, Long> delta) {
        this.counts = counts;
        this.vnodes = vnodes;
        this.total = total;
        this.delta = delta;
    }

    public Map<String, Long> getCounts() { return counts; }
    public void setCounts(Map<String, Long> counts) { this.counts = counts; }

    public Map<String, Integer> getVnodes() { return vnodes; }
    public void setVnodes(Map<String, Integer> vnodes) { this.vnodes = vnodes; }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public Map<String, Long> getDelta() { return delta; }
    public void setDelta(Map<String, Long> delta) { this.delta = delta; }
}
