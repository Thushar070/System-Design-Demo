# Autocomplete Search System using Trie & Redis — Exercise Guide

## Overview

This project implements an **Autocomplete Search System** using a **Prefix Trie** for top-K candidate matching and **Redis** for caching hot prefix queries in a Spring Boot 3.3 application.

- **Exercise Directory**: `/home/billy/Data/Projects/System-Design-Exercises/ex-7-autocomplete`
- **Application Stack**: Java 21, Spring Boot 3.3.0 (Port 8087), Spring Data Redis, Redis 7.2 (Port 6380 via Docker Compose), HTML5/CSS3 Dashboard.

---

## Folder Location & Working Directory

To execute and work on this exercise, ensure you are in the exercise directory:

```bash
cd /home/billy/Data/Projects/System-Design-Exercises/ex-7-autocomplete
```

---

## Architecture & How It Works

1. **Prefix Trie Data Structure**:
   - Stores terms character-by-character along tree paths.
   - Nodes hold search frequency counts to order recommendations descending.
2. **Redis Caching Layer**:
   - Hot queries are cached under `autocomplete:<prefix>:<topK>` with a 10-minute TTL.
   - Cache Hits return responses in `< 1 ms` with header `X-Cache: HIT`.
   - Cache Misses fall back to Trie search, populate Redis, and return `X-Cache: MISS`.

---

## How to Run

### Method 1: All-in-One Automated Script (Recommended)

Run the full automated setup, build, unit test, Trie search demo, and Redis cache hit/miss demo:

```bash
cd /home/billy/Data/Projects/System-Design-Exercises/ex-7-autocomplete
./run_lab.sh
```

---

### Method 2: Manual Execution

#### Step 1: Start Redis in Docker

```bash
cd /home/billy/Data/Projects/System-Design-Exercises/ex-7-autocomplete
./setup.sh
```

#### Step 2: Run Unit Tests

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn test
```

#### Step 3: Run Spring Boot Application

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn spring-boot:run
```

The app will start on port `8087`.

---

## Dashboards & API Interfaces

- **Interactive Visual Dashboard**: [http://localhost:8087/](http://localhost:8087/)
- **Swagger REST API Documentation**: [http://localhost:8087/swagger-ui.html](http://localhost:8087/swagger-ui.html)

---

## Demonstration & Verification Commands

### 1. Perform Autocomplete Search (1st call -> Cache MISS)
```bash
curl -i "http://localhost:8087/api/autocomplete/search?q=app&k=5"
```

### 2. Perform Autocomplete Search (2nd call -> Cache HIT)
```bash
curl -i "http://localhost:8087/api/autocomplete/search?q=app&k=5"
```

### 3. Insert or Boost Search Term
```bash
curl -s -X POST http://localhost:8087/api/autocomplete/terms \
  -H "Content-Type: application/json" \
  -d '{"term":"apple vision pro","frequency":95000}'
```

### 4. View Redis Cache Metrics
```bash
curl -s http://localhost:8087/api/autocomplete/cache/stats
```

---

## Analysis & Lab Write-Up

Detailed answers to all laboratory questions are documented in [`docs/analysis.md`](analysis.md):
1. Roles of Trie, Prefix, Redis Cache, and Top-K Suggestions.
2. Trie search determination mechanics.
3. Cache Hit processing flow.
4. Cache Miss processing flow.
5. Comparison of Trie-based searching vs linear database searching.

---

## Clean Up

To stop containers and wipe volumes:

```bash
docker compose down -v
```
