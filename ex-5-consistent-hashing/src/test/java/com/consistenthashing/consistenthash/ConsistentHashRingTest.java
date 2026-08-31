package com.consistenthashing.consistenthash;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ConsistentHashRingTest {

    private ConsistentHashRing ring;
    private StorageNode node1;
    private StorageNode node2;
    private StorageNode node3;

    @BeforeEach
    void setUp() {
        node1 = new StorageNode("localhost", 27017);
        node2 = new StorageNode("localhost", 27018);
        node3 = new StorageNode("localhost", 27019);
        ring = new ConsistentHashRing(150, List.of(node1, node2, node3));
    }

    @Test
    void testInitialSetup() {
        assertEquals(3, ring.getAllNodes().size());
        assertEquals(450, ring.getRingSize());
    }

    @Test
    void testDeterministicRouting() {
        StorageNode target1 = ring.getNode("1001");
        StorageNode target2 = ring.getNode("1001");
        assertNotNull(target1);
        assertEquals(target1, target2);
    }

    @Test
    void testAddNode() {
        StorageNode node4 = new StorageNode("localhost", 27020);
        assertTrue(ring.addNode(node4));
        assertEquals(4, ring.getAllNodes().size());
        assertEquals(600, ring.getRingSize());
    }

    @Test
    void testRemoveNode() {
        assertTrue(ring.removeNode(node1));
        assertEquals(2, ring.getAllNodes().size());
        assertEquals(300, ring.getRingSize());
        assertFalse(ring.getAllNodes().contains(node1));
    }

    @Test
    void testVirtualNodeCounts() {
        Map<String, Integer> counts = ring.getVirtualNodeCounts();
        assertEquals(150, counts.get("localhost:27017"));
        assertEquals(150, counts.get("localhost:27018"));
        assertEquals(150, counts.get("localhost:27019"));
    }
}
