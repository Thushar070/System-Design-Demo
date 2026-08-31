# API Rate Limiter using Token Bucket and Redis — Exercise Guide

## Overview

This project implements an **API Rate Limiter** using the **Token Bucket Algorithm** backed by **Redis** and **Redis Lua Scripting** in a Spring Boot 3.3 application.

- **Exercise Directory**: `/home/billy/Data/Projects/System-Design-Exercises/ex-6-rate-limiter`
- **Application Stack**: Java 21, Spring Boot 3.3.0 (Port 8086), Spring Data Redis, Redis 7.2 (via Docker Compose), HTML5/CSS3 Dashboard.

---

## Folder Location & Working Directory

To execute and work on this exercise, ensure you are in the exercise directory:

```bash
cd /home/billy/Data/Projects/System-Design-Exercises/ex-6-rate-limiter
```

---

## Architecture & How It Works

1. **Token Bucket Algorithm**:
   - Each client (identified by `X-Client-ID` header or IP address) has a token bucket with configurable **Capacity** ($C = 10$) and **Refill Rate** ($r = 2$ tokens/sec).
   - Incoming requests consume 1 token. When tokens reach 0, HTTP 429 Too Many Requests is returned.
2. **Atomic Redis Lua Scripting**:
   - Token status and last refill timestamp are evaluated atomically inside Redis via Lua script (`token_bucket.lua`).
   - Prevents race conditions under high concurrent traffic across multiple application nodes.
3. **HTTP Rate Limit Headers**:
   - `X-RateLimit-Limit`: Maximum bucket capacity.
   - `X-RateLimit-Remaining`: Tokens remaining after request execution.
   - `Retry-After`: Returned on HTTP 429 to inform client when to retry.

---

## How to Run

### Method 1: All-in-One Automated Script (Recommended)

Run the full automated setup, build, unit test, normal traffic, burst traffic, and refill demo script:

```bash
cd /home/billy/Data/Projects/System-Design-Exercises/ex-6-rate-limiter
./run_lab.sh
```

---

### Method 2: Manual Execution

#### Step 1: Start Redis in Docker

```bash
cd /home/billy/Data/Projects/System-Design-Exercises/ex-6-rate-limiter
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

The app will start on port `8086`.

---

## Dashboards & API Interfaces

- **Interactive Visual Dashboard**: [http://localhost:8086/](http://localhost:8086/)
- **Swagger REST API Documentation**: [http://localhost:8086/swagger-ui.html](http://localhost:8086/swagger-ui.html)

---

## Demonstration & Verification Commands

### 1. Send Single Allowed Request (HTTP 200)
```bash
curl -i -H "X-Client-ID: client_1" http://localhost:8086/api/protected/greeting
```

### 2. Simulate Burst Traffic (Trigger HTTP 429)
```bash
for i in {1..12}; do
  curl -s -o /dev/null -w "Request #$i: HTTP %{http_code}\n" -H "X-Client-ID: burst_user" http://localhost:8086/api/protected/greeting
done
```

### 3. Check Client Rate Limit Status
```bash
curl -s http://localhost:8086/api/ratelimit/status/client_1
```

### 4. Configure Client Limits Dynamically
```bash
curl -s -X POST http://localhost:8086/api/ratelimit/configure \
  -H "Content-Type: application/json" \
  -d '{"clientId":"vip_user","capacity":50,"refillRate":10}'
```

---

## Analysis & Lab Write-Up

Detailed answers to all laboratory questions are documented in [`docs/analysis.md`](analysis.md):
1. Roles of Token Bucket, Bucket Capacity, Refill Rate, and Redis.
2. How the Token Bucket algorithm allows or rejects requests.
3. Why Redis is required when multiple application instances are used.
4. Comparison of Fixed Window, Sliding Window Log, Sliding Window Counter, and Token Bucket.
5. Analysis of system behaviour under normal vs. burst traffic.

---

## Clean Up

To stop containers and wipe volumes:

```bash
docker compose down -v
```
