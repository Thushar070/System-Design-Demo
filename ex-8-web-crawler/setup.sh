#!/bin/bash
set -e

echo "=== Starting Redis service for Web Crawler (Port 6381) ==="
docker compose up -d redis

echo "=== Waiting for Redis to accept connections ==="
until docker compose exec -T redis redis-cli ping 2>/dev/null | grep -q PONG; do
    echo "  waiting for redis..."
    sleep 1
done
echo "=== Redis is ready on port 6381 ==="

echo "=== Start Spring Boot app: JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn spring-boot:run ==="
echo "=== Open Dashboard:        http://localhost:8080/ ==="
echo "=== Swagger Docs:          http://localhost:8080/swagger-ui.html ==="

