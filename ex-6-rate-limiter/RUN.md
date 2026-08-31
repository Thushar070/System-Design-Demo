# Exercise 6 — API Rate Limiter using Token Bucket

## Directory to Run
```bash
cd /home/billy/Data/Projects/System-Design-Exercises/ex-6-rate-limiter
```

---

## Method 1: Automated Script (Recommended)

```bash
chmod +x run_lab.sh setup.sh
./run_lab.sh
```

---

## Method 2: Manual Execution

### Step 1: Start Redis via Docker
```bash
./setup.sh
```

### Step 2: Run Unit Tests
```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn test
```

### Step 3: Run Spring Boot Application
```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn spring-boot:run
```

---

## Method 3: Run Everything in Docker

```bash
docker compose up -d --build
```

---

## Access & Endpoints

- **Web Dashboard**: http://localhost:8086/
- **Swagger API Docs**: http://localhost:8086/swagger-ui.html

---

## Clean Up / Stop Docker Containers
```bash
docker compose down -v
```
