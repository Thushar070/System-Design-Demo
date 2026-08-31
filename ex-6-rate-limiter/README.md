# API Rate Limiter using Token Bucket and Redis — Lab Exercise 6

**UCS3513 System Design Laboratory** — Sri Sivasubramaniya Nadar College of Engineering

A Spring Boot application implementing a distributed **API Rate Limiter** using the **Token Bucket Algorithm** backed by **Redis** and atomic **Redis Lua Scripting**.

---

## Folder Location
`./ex-6-rate-limiter`

---

## Quick Start

```bash
# 1. Navigate to directory
cd ex-6-rate-limiter

# 2. Run one-command automated lab demo (Redis + App in Docker + traffic tests)
./run_lab.sh

# OR run manually:
./setup.sh
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn spring-boot:run
```

- **Dashboard UI**: [http://localhost:8080/](http://localhost:8080/)
- **Swagger API Docs**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Documentation & Analysis

See [`docs/README.md`](docs/README.md) for full execution guides and [`docs/analysis.md`](docs/analysis.md) for written answers to analysis questions.
