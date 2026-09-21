# Exercise 8 — Web Crawler for Automated Web Content Discovery

## Directory to Run
```bash
cd /home/billy/Data/Projects/System-Design-Exercises/ex-8-web-crawler
```

---

## Method 1: Automated Script (Recommended)

```bash
chmod +x run_lab.sh setup.sh
./run_lab.sh
```

---

## Method 2: Manual Execution

### Step 1: Start Redis via Docker (Port 6381)
```bash
./setup.sh
```

### Step 2: Run Unit Tests
```bash
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn test
```

### Step 3: Run Spring Boot Application (Port 8080)
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

- **Web Dashboard**: http://localhost:8080/
- **Swagger API Docs**: http://localhost:8080/swagger-ui.html
- **Mock Web Portal**: http://localhost:8080/mock-web/index.html

---

## Clean Up / Stop Docker Containers
```bash
docker compose down -v
```

