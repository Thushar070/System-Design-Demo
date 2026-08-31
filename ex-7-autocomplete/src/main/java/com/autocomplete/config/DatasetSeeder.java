package com.autocomplete.config;

import com.autocomplete.service.AutocompleteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DatasetSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatasetSeeder.class);

    private final AutocompleteService autocompleteService;

    public DatasetSeeder(AutocompleteService autocompleteService) {
        this.autocompleteService = autocompleteService;
    }

    @Override
    public void run(String... args) {
        log.info("Seeding Autocomplete Trie dataset...");

        Map<String, Long> dataset = Map.ofEntries(
                Map.entry("apple", 15000L),
                Map.entry("application", 12000L),
                Map.entry("app store", 9500L),
                Map.entry("appointment", 8000L),
                Map.entry("apple watch", 7500L),
                Map.entry("apple music", 6000L),
                Map.entry("amazon", 50000L),
                Map.entry("amazon prime", 35000L),
                Map.entry("amazon prime video", 20000L),
                Map.entry("google", 90000L),
                Map.entry("google maps", 45000L),
                Map.entry("google docs", 30000L),
                Map.entry("google drive", 28000L),
                Map.entry("github", 40000L),
                Map.entry("git bash", 18000L),
                Map.entry("gitlab", 12000L),
                Map.entry("youtube", 85000L),
                Map.entry("facebook", 65000L),
                Map.entry("instagram", 70000L),
                Map.entry("stack overflow", 38000L),
                Map.entry("spring boot", 25000L),
                Map.entry("spring security", 15000L),
                Map.entry("spring data redis", 11000L),
                Map.entry("system design", 30000L),
                Map.entry("system architecture", 14000L),
                Map.entry("system admin", 9000L),
                Map.entry("trie data structure", 16000L),
                Map.entry("redis cache", 22000L),
                Map.entry("redis pubsub", 13000L),
                Map.entry("redis cluster", 18000L),
                Map.entry("python", 55000L),
                Map.entry("javascript", 60000L),
                Map.entry("java 21", 27000L)
        );

        dataset.forEach(autocompleteService::addSearchTerm);
        log.info("Completed seeding {} terms into Trie.", dataset.size());
    }
}
