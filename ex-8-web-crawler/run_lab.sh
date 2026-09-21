#!/usr/bin/env bash
set -e

ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$ROOT"

echo "=============================================="
echo "Lab Exercise 8: Web Crawler Lab Orchestration"
echo "=============================================="

# 1. Start Redis service
echo "Starting Redis service..."
docker compose up -d redis

echo -n "Waiting for Redis..."
until docker compose exec -T redis redis-cli ping 2>/dev/null | grep -q "PONG"; do
    echo -n "."
    sleep 1
done
echo " Ready!"

# 2. Build Spring Boot application target JAR
echo ""
echo "Packaging Spring Boot application target JAR..."
if [ -d "/usr/lib/jvm/java-21-openjdk-amd64" ]; then
    JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn -q -DskipTests package
else
    mvn -q -DskipTests package
fi

# 3. Build & start Spring Boot app container
echo "Building & starting Spring Boot app (docker on port 8080)..."
docker compose up -d --build --force-recreate app

# 4. Wait for application to be ready
echo -n "Waiting for application on http://localhost:8080 ..."
until curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/crawler/status 2>/dev/null | grep -q "200"; do
    echo -n "."
    sleep 2
done
echo " Ready!"

# 5. Execute benchmarks and report generation
echo ""
python3 scripts/load_test.py demo
echo ""
python3 scripts/load_test.py benchmark
echo ""
python3 scripts/load_test.py concurrent
echo ""
python3 scripts/gen_report.py || true

echo "=============================================="
echo "Lab Exercise 8 Execution Finished Successfully!"
echo "Dashboard UI: http://localhost:8080/"
echo "Swagger UI:   http://localhost:8080/swagger-ui.html"
echo "Mock Web:     http://localhost:8080/mock-web/index.html"
echo "Report:       SD_A8.docx & SD_A8_Web_Crawler.pdf"
echo "Cleanup:      docker compose down -v"
echo "=============================================="
