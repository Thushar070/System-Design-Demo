package com.consistenthashing.service;

import com.consistenthashing.consistenthash.ConsistentHashRing;
import com.consistenthashing.consistenthash.StorageNode;
import com.consistenthashing.dto.MigrationReport;
import com.consistenthashing.model.Student;
import com.consistenthashing.storage.StudentStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class NodeService {

    private static final Logger log = LoggerFactory.getLogger(NodeService.class);

    private final ConsistentHashRing hashRing;
    private final StudentStore studentStore;
    private final DistributionService distributionService;

    public NodeService(ConsistentHashRing hashRing, StudentStore studentStore, DistributionService distributionService) {
        this.hashRing = hashRing;
        this.studentStore = studentStore;
        this.distributionService = distributionService;
    }

    public MigrationReport addNode(String host, int port) {
        StorageNode newNode = new StorageNode(host, port);

        if (!studentStore.isReachable(newNode)) {
            throw new IllegalArgumentException("Cannot connect to MongoDB node at " + newNode.getIdentifier() +
                ". Ensure the host container is running (e.g. mongo1, mongo2, mongo3, mongo4).");
        }

        Map<String, Long> beforeCounts = distributionService.getDistributionReport().getCounts();

        boolean added = hashRing.addNode(newNode);
        if (!added) {
            log.info("Node {} already registered on hash ring", newNode.getIdentifier());
            Map<String, Long> afterCounts = distributionService.getDistributionReport().getCounts();
            return new MigrationReport(0, beforeCounts, afterCounts);
        }
        log.info("Added node {} to the hash ring", newNode.getIdentifier());

        int migratedCount = 0;
        try {
            Set<StorageNode> allNodes = hashRing.getAllNodes();

            for (StorageNode existingNode : allNodes) {
                if (existingNode.equals(newNode)) continue;

                List<Student> students = studentStore.findAll(existingNode);
                for (Student student : students) {
                    StorageNode targetNode = hashRing.getNode(student.getRollNo());
                    if (targetNode.equals(newNode)) {
                        studentStore.save(newNode, student);
                        studentStore.deleteByRollNo(existingNode, student.getRollNo());
                        migratedCount++;
                    }
                }
            }
        } catch (Exception e) {
            hashRing.removeNode(newNode);
            throw new RuntimeException("Migration failed while adding node " + newNode.getIdentifier() + ": " + e.getMessage(), e);
        }

        log.info("Add-node migration complete: {} record(s) moved to {}", migratedCount, newNode.getIdentifier());
        Map<String, Long> afterCounts = distributionService.getDistributionReport().getCounts();
        return new MigrationReport(migratedCount, beforeCounts, afterCounts);
    }

    public MigrationReport removeNode(String host, int port) {
        StorageNode targetNode = new StorageNode(host, port);
        Map<String, Long> beforeCounts = distributionService.getDistributionReport().getCounts();

        if (!hashRing.getAllNodes().contains(targetNode)) {
            log.info("Node {} not found on hash ring", targetNode.getIdentifier());
            Map<String, Long> afterCounts = distributionService.getDistributionReport().getCounts();
            return new MigrationReport(0, beforeCounts, afterCounts);
        }

        List<Student> orphanStudents = studentStore.findAll(targetNode);
        log.info("Removing node {} from ring; relocating {} record(s)", targetNode.getIdentifier(), orphanStudents.size());

        hashRing.removeNode(targetNode);
        int relocatedCount = 0;

        for (Student student : orphanStudents) {
            StorageNode newOwner = hashRing.getNode(student.getRollNo());
            if (newOwner != null && !newOwner.equals(targetNode)) {
                studentStore.save(newOwner, student);
                relocatedCount++;
            }
        }

        studentStore.clear(targetNode);

        log.info("Remove-node migration complete: {} record(s) relocated", relocatedCount);
        Map<String, Long> afterCounts = distributionService.getDistributionReport().getCounts();
        return new MigrationReport(relocatedCount, beforeCounts, afterCounts);
    }

    public Collection<StorageNode> getRegisteredNodes() {
        return hashRing.getAllNodes();
    }
}
