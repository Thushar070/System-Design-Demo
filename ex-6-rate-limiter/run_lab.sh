#!/bin/bash
echo "=============================================="
echo "API Rate Limiter Lab (Token Bucket & Redis)"
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
echo "Building & starting Spring Boot app (docker on port 8086)..."
docker compose up -d --build --force-recreate app

echo -n "Waiting for app on http://localhost:8086 ..."
until curl -s -o /dev/null -w "%{http_code}" http://localhost:8086/api/ratelimit/defaults 2>/dev/null | grep -q "200"; do
    echo -n "."
    sleep 2
done
echo " Ready!"

echo ""
echo ">>> DEMO 1: NORMAL TRAFFIC (Single request from client_demo) <<<"
curl -s -i http://localhost:8086/api/protected/greeting -H "X-Client-ID: client_demo" | head -n 12

echo ""
echo ">>> DEMO 2: BURST TRAFFIC (Sending 12 rapid requests; capacity = 10) <<<"
for i in {1..12}; do
    STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8086/api/protected/greeting -H "X-Client-ID: client_demo")
    echo "Request #$i -> HTTP $STATUS"
done

echo ""
echo ">>> DEMO 3: REJECTED REQUEST DETAILS (HTTP 429) <<<"
curl -s -i http://localhost:8086/api/protected/greeting -H "X-Client-ID: client_demo" | head -n 15

echo ""
echo ">>> DEMO 4: REFILL OBSERVATION (Waiting 3 seconds for token refill) <<<"
sleep 3
echo "Sending request after 3s refill (2 tokens/sec added):"
curl -s -i http://localhost:8086/api/protected/greeting -H "X-Client-ID: client_demo" | head -n 12

echo ""
echo "=============================================="
echo "Lab Execution Complete!"
echo "Dashboard UI: http://localhost:8086/"
echo "Swagger UI:   http://localhost:8086/swagger-ui.html"
echo "Cleanup:      docker compose down -v"
echo "=============================================="
