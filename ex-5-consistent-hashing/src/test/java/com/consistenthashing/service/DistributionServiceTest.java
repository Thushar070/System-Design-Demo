package com.consistenthashing.service;

import com.consistenthashing.consistenthash.ConsistentHashRing;
import com.consistenthashing.consistenthash.StorageNode;
import com.consistenthashing.dto.CreateStudentRequest;
import com.consistenthashing.dto.DistributionReport;
import com.consistenthashing.storage.InMemoryStudentStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DistributionServiceTest {

    private DistributionService distributionService;
    private StudentService studentService;
    private ConsistentHashRing ring;
    private InMemoryStudentStore store;

    @BeforeEach
    void setUp() {
        StorageNode n1 = new StorageNode("localhost", 27017);
        StorageNode n2 = new StorageNode("localhost", 27018);
        StorageNode n3 = new StorageNode("localhost", 27019);
        ring = new ConsistentHashRing(150, List.of(n1, n2, n3));
        store = new InMemoryStudentStore();
        studentService = new StudentService(ring, store);
        distributionService = new DistributionService(ring, store);
    }

    @Test
    void testEmptyDistribution() {
        DistributionReport report = distributionService.getDistributionReport();
        assertEquals(0, report.getTotal());
        assertEquals(3, report.getCounts().size());
    }

    @Test
    void testDistributionReportWithData() {
        for (int i = 1001; i <= 1010; i++) {
            studentService.createStudent(new CreateStudentRequest(String.valueOf(i), "S_" + i, "IT", 2));
        }

        DistributionReport report = distributionService.getDistributionReport();
        assertEquals(10, report.getTotal());
    }
}
