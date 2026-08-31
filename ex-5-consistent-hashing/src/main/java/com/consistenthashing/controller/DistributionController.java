package com.consistenthashing.controller;

import com.consistenthashing.dto.DistributionReport;
import com.consistenthashing.service.DistributionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/distribution")
public class DistributionController {

    private final DistributionService distributionService;

    public DistributionController(DistributionService distributionService) {
        this.distributionService = distributionService;
    }

    @GetMapping
    public ResponseEntity<DistributionReport> getDistributionReport() {
        return ResponseEntity.ok(distributionService.getDistributionReport());
    }

    @GetMapping("/counts")
    public ResponseEntity<Map<String, Long>> getCounts() {
        return ResponseEntity.ok(distributionService.getDistributionReport().getCounts());
    }
}
