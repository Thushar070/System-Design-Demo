package com.autocomplete.controller;

import com.autocomplete.dto.AddTermRequest;
import com.autocomplete.dto.AutocompleteResponse;
import com.autocomplete.dto.CacheStatsDto;
import com.autocomplete.service.AutocompleteService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/autocomplete")
public class AutocompleteController {

    private final AutocompleteService autocompleteService;

    public AutocompleteController(AutocompleteService autocompleteService) {
        this.autocompleteService = autocompleteService;
    }

    @GetMapping("/search")
    public ResponseEntity<AutocompleteResponse> search(
            @RequestParam(name = "q", defaultValue = "") String query,
            @RequestParam(name = "k", required = false) Integer topK) {

        AutocompleteResponse res = autocompleteService.search(query, topK);

        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Cache", res.getCacheSource());

        return ResponseEntity.ok().headers(headers).body(res);
    }

    @PostMapping("/terms")
    public ResponseEntity<Map<String, String>> addTerm(@RequestBody AddTermRequest request) {
        if (request.getTerm() == null || request.getTerm().trim().isEmpty()) {
            throw new IllegalArgumentException("term cannot be empty");
        }
        long freq = request.getFrequency() > 0 ? request.getFrequency() : 1;
        autocompleteService.addSearchTerm(request.getTerm(), freq);

        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Search term '" + request.getTerm() + "' added/updated with frequency " + freq
        ));
    }

    @GetMapping("/cache/stats")
    public ResponseEntity<CacheStatsDto> getCacheStats() {
        return ResponseEntity.ok(autocompleteService.getCacheStats());
    }

    @DeleteMapping("/cache")
    public ResponseEntity<Map<String, String>> clearCache() {
        autocompleteService.clearCache();
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Redis autocomplete cache cleared successfully."
        ));
    }
}
