# Consistent Hashing Distributed Data Routing — Exercise Guide

## Overview

This lab demonstrates **Application-Level Consistent Hashing** with virtual nodes to route student records across multiple independent MongoDB storage nodes.

- **Exercise Directory**: `/home/billy/Data/Projects/System-Design-Exercises/ex-5-consistent-hashing`
- **Application Stack**: Java 21, Spring Boot 3.3.0 (Port 8085), MongoDB 7.0 (via Docker Compose), HTML5/CSS3 Dashboard.

---

## Folder Location & Working Directory

To execute and work on this exercise, ensure you are in the exercise directory:

```bash
cd /home/billy/Data/Projects/System-Design-Exercises/ex-5-consistent-hashing
```

---

## Architecture & How It Works

1. **Hash Ring**: The application maintains a circular keyspace using a `TreeMap<Long, StorageNode>`. Positions are calculated using `SHA-256(key)`.
2. **Virtual Nodes**: Each physical MongoDB storage node registers 150 virtual nodes on the hash ring (`nodeId#0` to `nodeId#149`). Virtual nodes ensure uniform data distribution and prevent hotspotting.
3. **Data Routing**:
   - For a `Student` record with `rollNo`, `SHA-256(rollNo)` determines its ring position.
   - The lookup finds the first virtual node clockwise (`ceilingEntry`) on the ring, identifying the target MongoDB instance.
   - Reads and writes bypass central routing routers (`mongos`) and go directly to the target MongoDB container.
4. **Dynamic Node Addition/Removal**:
   - **Add Node**: Inserting a new node places its virtual nodes onto the ring. Only ~1/N of existing records migrate to the new node; no unaffected records are moved.
   - **Remove Node**: Removing a node relocates only the records owned by that node across the remaining nodes without data loss.

---

## How to Run

### Method 1: All-in-One Automated Script (Recommended)

Run the full automated setup, build, seed, and dynamic node addition/removal demonstration script:

```bash
cd /home/billy/Data/Projects/System-Design-Exercises/ex-5-consistent-hashing
./run_lab.sh
```

---

### Method 2: Manual Execution

#### Step 1: Start MongoDB Storage Nodes

Start the Docker containers hosting MongoDB instances (`mongo1`, `mongo2`, `mongo3`, `mongo4`):

```bash
cd /home/billy/Data/Projects/System-Design-Exercises/ex-5-consistent-hashing
./setup.sh
```

#### Step 2: Build & Run Unit Tests

Execute unit tests to verify hash ring logic, data migration, and routing without needing Docker:

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn test
```

#### Step 3: Run the Spring Boot Application

Launch the application:

```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn spring-boot:run
```

The app will start on port `8085` and auto-seed 20 student records (Roll Nos 1001-1020).

---

## Dashboards & API Interfaces

- **Interactive Visual Dashboard**: [http://localhost:8085/](http://localhost:8085/)
- **Swagger REST API Documentation**: [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)
- **Mongo Express Database GUIs**:
  - Node 1 (`mongo1:27017`): [http://localhost:8081](http://localhost:8081)
  - Node 2 (`mongo2:27018`): [http://localhost:8082](http://localhost:8082)
  - Node 3 (`mongo3:27019`): [http://localhost:8083](http://localhost:8083)
  - Node 4 (`mongo4:27020`): [http://localhost:8084](http://localhost:8084)

---

## Demonstration & Verification Commands

### 1. View Data Distribution across Active Nodes
```bash
curl -s http://localhost:8085/api/distribution | jq '{total, counts}'
```

### 2. Add a 4th Storage Node dynamically (`mongo4`)
```bash
curl -s -X POST http://localhost:8085/api/nodes \
  -H 'Content-Type: application/json' \
  -d '{"host":"mongo4","port":27017}' | jq '{migrated, before, after}'
```

### 3. Verify Distribution After Node Addition
```bash
curl -s http://localhost:8085/api/distribution | jq '{total, counts}'
```
*Notice that only ~25% of records migrate to `mongo4` while remaining records stay in place.*

### 4. Remove Storage Node (`mongo4`)
```bash
curl -s -X DELETE http://localhost:8085/api/nodes/mongo4/27017 | jq '{migrated, before, after}'
```

### 5. Verify Distribution Reverted
```bash
curl -s http://localhost:8085/api/distribution | jq '{total, counts}'
```
*Data distribution cleanly reverts back to the original layout with 0 data loss.*

---

## Analysis & Lab Write-Up

Detailed answers to all laboratory questions are documented in [`docs/analysis.md`](analysis.md):
1. Roles of Hash Ring, Virtual Nodes, and MongoDB Storage Nodes.
2. Consistent Hashing storage location determination algorithm.
3. Node Addition migration mechanics.
4. Node Removal redistribution mechanics.
5. Modulo-based hashing vs. Consistent Hashing comparison.
6. Application-level Consistent Hashing vs. MongoDB Horizontal Sharding comparison.

---

## Clean Up

To stop containers and wipe volumes:

```bash
docker compose down -v
```
