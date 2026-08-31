package com.consistenthashing.consistenthash;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class ConsistentHashRing {

    private final int numberOfReplicas;
    private final ConcurrentSkipListMap<Long, StorageNode> circle = new ConcurrentSkipListMap<>();
    private final Set<StorageNode> physicalNodes = ConcurrentHashMap.newKeySet();

    public ConsistentHashRing(int numberOfReplicas, Collection<StorageNode> nodes) {
        this.numberOfReplicas = numberOfReplicas;
        if (nodes != null) {
            for (StorageNode node : nodes) {
                addNode(node);
            }
        }
    }

    public synchronized boolean addNode(StorageNode node) {
        if (node == null || physicalNodes.contains(node)) {
            return false;
        }
        physicalNodes.add(node);
        for (int i = 0; i < numberOfReplicas; i++) {
            String vnodeKey = node.getIdentifier() + "#" + i;
            long hash = HashFunction.hash(vnodeKey);
            circle.put(hash, node);
        }
        return true;
    }

    public synchronized boolean removeNode(StorageNode node) {
        if (node == null || !physicalNodes.contains(node)) {
            return false;
        }
        physicalNodes.remove(node);
        for (int i = 0; i < numberOfReplicas; i++) {
            String vnodeKey = node.getIdentifier() + "#" + i;
            long hash = HashFunction.hash(vnodeKey);
            circle.remove(hash, node);
        }
        return true;
    }

    public StorageNode getNode(String key) {
        if (circle.isEmpty()) {
            return null;
        }
        if (key == null) {
            return circle.firstEntry().getValue();
        }
        long hash = HashFunction.hash(key);
        Map.Entry<Long, StorageNode> entry = circle.ceilingEntry(hash);
        if (entry == null) {
            entry = circle.firstEntry();
        }
        return entry != null ? entry.getValue() : null;
    }

    public Set<StorageNode> getAllNodes() {
        return Collections.unmodifiableSet(new HashSet<>(physicalNodes));
    }

    public int getNumberOfReplicas() {
        return numberOfReplicas;
    }

    public Map<String, Integer> getVirtualNodeCounts() {
        Map<String, Integer> counts = new HashMap<>();
        for (StorageNode node : physicalNodes) {
            counts.put(node.getIdentifier(), 0);
        }
        for (StorageNode node : circle.values()) {
            counts.put(node.getIdentifier(), counts.getOrDefault(node.getIdentifier(), 0) + 1);
        }
        return counts;
    }

    public int getRingSize() {
        return circle.size();
    }
}
