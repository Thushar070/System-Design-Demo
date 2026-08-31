#!/bin/bash
echo "=============================================="
echo "Autocomplete Search System (Trie & Redis)"
echo "=============================================="

echo "Starting Redis service..."
docker compose up -d redis

echo -n "Waiting for Redis..."
until docker compose exec -T redis redis-cli ping 2>/dev/null | grep -q "PONG"; do
    echo -n "."
    sleep 1
done
echo " Ready!"

echo ""
echo "Packaging Spring Boot application target JAR..."
if [ -d "/usr/lib/jvm/java-21-openjdk-amd64" ]; then
    JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 mvn -q -DskipTests package
else
    mvn -q -DskipTests package
fi

echo "Building & starting Spring Boot app (docker on port 8087)..."
docker compose up -d --build --force-recreate app

echo -n "Waiting for app on http://localhost:8087 ..."
until curl -s -o /dev/null -w "%{http_code}" http://localhost:8087/api/autocomplete/cache/stats 2>/dev/null | grep -q "200"; do
    echo -n "."
    sleep 2
done
echo " Ready!"

echo ""
echo ">>> DEMO 1: FIRST SEARCH 'app' (CACHE MISS -> Searches Trie) <<<"
curl -s -i "http://localhost:8087/api/autocomplete/search?q=app&k=5" | head -n 15

echo ""
echo ">>> DEMO 2: SECOND SEARCH 'app' (CACHE HIT -> Served from Redis) <<<"
curl -s -i "http://localhost:8087/api/autocomplete/search?q=app&k=5" | head -n 15

echo ""
echo ">>> DEMO 3: SEARCH 'goog' <<<"
curl -s "http://localhost:8087/api/autocomplete/search?q=goog&k=3" | jq '{query, cacheSource, executionTimeMs, suggestions}'

echo ""
echo ">>> DEMO 4: REDIS CACHE STATS <<<"
curl -s "http://localhost:8087/api/autocomplete/cache/stats" | jq '.'

echo ""
echo "=============================================="
echo "Lab Execution Complete!"
echo "Dashboard UI: http://localhost:8087/"
echo "Swagger UI:   http://localhost:8087/swagger-ui.html"
echo "Cleanup:      docker compose down -v"
echo "=============================================="
