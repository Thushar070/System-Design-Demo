package com.consistenthashing.service;

import com.consistenthashing.consistenthash.ConsistentHashRing;
import com.consistenthashing.consistenthash.StorageNode;
import com.consistenthashing.dto.CreateStudentRequest;
import com.consistenthashing.dto.MigrationReport;
import com.consistenthashing.storage.InMemoryStudentStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NodeServiceTest {

    private NodeService nodeService;
    private StudentService studentService;
    private DistributionService distributionService;
    private ConsistentHashRing ring;
    private InMemoryStudentStore store;

    @BeforeEach
    void setUp() {
        StorageNode n1 = new StorageNode("localhost", 27017);
        StorageNode n2 = new StorageNode("localhost", 27018);
        StorageNode n3 = new StorageNode("localhost", 27019);
        ring = new ConsistentHashRing(150, List.of(n1, n2, n3));
        store = new InMemoryStudentStore();
        distributionService = new DistributionService(ring, store);
        studentService = new StudentService(ring, store);
        nodeService = new NodeService(ring, store, distributionService);
    }

    @Test
    void testAddNodeMigration() {
        for (int i = 1001; i <= 1020; i++) {
            studentService.createStudent(new CreateStudentRequest(String.valueOf(i), "Student_" + i, "CSE", 1));
        }

        assertEquals(20, distributionService.getDistributionReport().getTotal());

        MigrationReport report = nodeService.addNode("localhost", 27020);
        assertNotNull(report);

        // Total count must remain 20
        assertEquals(20, distributionService.getDistributionReport().getTotal());
        assertEquals(4, ring.getAllNodes().size());
    }

    @Test
    void testRemoveNodeMigration() {
        for (int i = 1001; i <= 1020; i++) {
            studentService.createStudent(new CreateStudentRequest(String.valueOf(i), "Student_" + i, "CSE", 1));
        }

        nodeService.addNode("localhost", 27020);
        MigrationReport removeReport = nodeService.removeNode("localhost", 27020);

        assertNotNull(removeReport);
        assertEquals(20, distributionService.getDistributionReport().getTotal());
        assertEquals(3, ring.getAllNodes().size());
    }
}
