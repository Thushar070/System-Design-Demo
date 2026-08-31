package com.consistenthashing.controller;

import com.consistenthashing.consistenthash.ConsistentHashRing;
import com.consistenthashing.consistenthash.StorageNode;
import com.consistenthashing.dto.CreateNodeRequest;
import com.consistenthashing.dto.MigrationReport;
import com.consistenthashing.service.NodeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/nodes")
public class NodeController {

    private final NodeService nodeService;
    private final ConsistentHashRing hashRing;

    public NodeController(NodeService nodeService, ConsistentHashRing hashRing) {
        this.nodeService = nodeService;
        this.hashRing = hashRing;
    }

    @GetMapping
    public ResponseEntity<Collection<StorageNode>> getRegisteredNodes() {
        return ResponseEntity.ok(nodeService.getRegisteredNodes());
    }

    @GetMapping("/ring")
    public ResponseEntity<Map<String, Integer>> getRingInfo() {
        return ResponseEntity.ok(hashRing.getVirtualNodeCounts());
    }

    @PostMapping
    public ResponseEntity<MigrationReport> addNode(@RequestBody CreateNodeRequest request) {
        MigrationReport report = nodeService.addNode(request.getHost(), request.getPort());
        return ResponseEntity.ok(report);
    }

    @DeleteMapping("/{host}/{port}")
    public ResponseEntity<MigrationReport> removeNode(@PathVariable String host, @PathVariable int port) {
        MigrationReport report = nodeService.removeNode(host, port);
        return ResponseEntity.ok(report);
    }
}
