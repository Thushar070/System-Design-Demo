package com.consistenthashing.dto;

import java.util.Map;

public class MigrationReport {
    private int migrated;
    private Map<String, Long> before;
    private Map<String, Long> after;

    public MigrationReport() {}

    public MigrationReport(int migrated, Map<String, Long> before, Map<String, Long> after) {
        this.migrated = migrated;
        this.before = before;
        this.after = after;
    }

    public int getMigrated() { return migrated; }
    public void setMigrated(int migrated) { this.migrated = migrated; }

    public Map<String, Long> getBefore() { return before; }
    public void setBefore(Map<String, Long> before) { this.before = before; }

    public Map<String, Long> getAfter() { return after; }
    public void setAfter(Map<String, Long> after) { this.after = after; }
}
