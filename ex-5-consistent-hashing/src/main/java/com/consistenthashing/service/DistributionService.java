package com.consistenthashing.service;

import com.consistenthashing.consistenthash.ConsistentHashRing;
import com.consistenthashing.consistenthash.StorageNode;
import com.consistenthashing.dto.DistributionReport;
import com.consistenthashing.storage.StudentStore;

import java.util.HashMap;
import java.util.Map;

public class DistributionService {

    private final ConsistentHashRing hashRing;
    private final StudentStore studentStore;

    public DistributionService(ConsistentHashRing hashRing, StudentStore studentStore) {
        this.hashRing = hashRing;
        this.studentStore = studentStore;
    }

    public DistributionReport getDistributionReport() {
        Map<String, Long> counts = new HashMap<>();
        long total = 0;

        for (StorageNode node : hashRing.getAllNodes()) {
            long c = 0;
            try {
                c = studentStore.count(node);
            } catch (Exception ignored) {
            }
            counts.put(node.getIdentifier(), c);
            total += c;
        }

        Map<String, Integer> vnodes = hashRing.getVirtualNodeCounts();
        Map<String, Long> delta = new HashMap<>(counts);

        return new DistributionReport(counts, vnodes, total, delta);
    }
}
