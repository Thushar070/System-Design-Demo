# Autocomplete Search System using Prefix Matching — Lab Exercise 7

**UCS3513 System Design Laboratory** — Sri Sivasubramaniya Nadar College of Engineering

A Spring Boot application implementing an **Autocomplete Search System** using a **Prefix Trie** for top-K candidate matching and **Redis** for caching hot queries.

---

## Folder Location
`/home/billy/Data/Projects/System-Design-Exercises/ex-7-autocomplete`

---

## Quick Start

```bash
# 1. Navigate to directory
cd /home/billy/Data/Projects/System-Design-Exercises/ex-7-autocomplete

# 2. Run one-command automated lab demo (Redis + App on Port 8087)
./run_lab.sh

# OR run manually:
./setup.sh
JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn spring-boot:run
```

- **Dashboard UI**: [http://localhost:8087/](http://localhost:8087/)
- **Swagger API Docs**: [http://localhost:8087/swagger-ui.html](http://localhost:8087/swagger-ui.html)

---

## Documentation & Analysis

See [`docs/README.md`](docs/README.md) for full execution guides and [`docs/analysis.md`](docs/analysis.md) for written answers to analysis questions.
