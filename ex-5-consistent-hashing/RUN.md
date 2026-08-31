# Quick Run Guide — Exercise 5: Consistent Hashing

## Exercise Directory Location
```bash
cd /home/billy/Data/Projects/System-Design-Exercises/ex-5-consistent-hashing
```

---

## Option 1: One-Command Fully Automated Execution (Recommended)

Run the automated lab script. It will spin up MongoDB containers in Docker, build the application (port 8085), seed 20 student records, and execute node addition/removal demos:

```bash
./run_lab.sh
```

---

## Option 2: Run Application inside Docker

```bash
docker compose up -d --build
```

After running, open your browser:
- **Web Dashboard**: [http://localhost:8085/](http://localhost:8085/)
- **Swagger UI**: [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)

---

## Option 3: Manual Execution (Run App on Host)

### Step 1: Start MongoDB Storage Nodes
```bash
./setup.sh
```

### Step 2: Run Unit Tests
```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn test
```

### Step 3: Start Spring Boot Application
```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn spring-boot:run
```

---

## Testing API Endpoints

### 1. View Data Distribution
```bash
curl -s http://localhost:8085/api/distribution | jq '{total, counts}'
```

### 2. Add Storage Node (mongo4)
```bash
curl -s -X POST http://localhost:8085/api/nodes -H 'Content-Type: application/json' -d '{"host":"mongo4","port":27017}'
```

### 3. Remove Storage Node (mongo4)
```bash
curl -s -X DELETE http://localhost:8085/api/nodes/mongo4/27017
```

---

## Clean Up / Stop Containers
```bash
docker compose down -v
```
