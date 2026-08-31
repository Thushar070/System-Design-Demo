Write-Host "==============================================" -ForegroundColor Cyan
Write-Host "API Rate Limiter Lab (Token Bucket & Redis)" -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan

docker compose up -d redis

Write-Host "Waiting for Redis..." -NoNewline
while ($(docker compose exec -T redis redis-cli ping 2>$null) -notmatch "PONG") {
    Start-Sleep -Seconds 1
    Write-Host "." -NoNewline
}
Write-Host " Ready!" -ForegroundColor Green

docker compose up -d --build app

Write-Host "Waiting for App..." -NoNewline
while ($(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/ratelimit/defaults 2>$null) -notmatch "200") {
    Start-Sleep -Seconds 2
    Write-Host "." -NoNewline
}
Write-Host " Ready!" -ForegroundColor Green

Write-Host "`n>>> DEMO: NORMAL TRAFFIC <<<" -ForegroundColor Yellow
Invoke-RestMethod -Uri "http://localhost:8080/api/protected/greeting" -Headers @{"X-Client-ID"="client_demo"}

Write-Host "`n>>> DEMO: BURST TRAFFIC (12 Requests) <<<" -ForegroundColor Yellow
1..12 | ForEach-Object {
    try {
        $res = Invoke-WebRequest -Uri "http://localhost:8080/api/protected/greeting" -Headers @{"X-Client-ID"="client_demo"} -UseBasicParsing
        Write-Host "Request #$_ -> HTTP 200 OK" -ForegroundColor Green
    } catch {
        Write-Host "Request #$_ -> HTTP 429 Too Many Requests" -ForegroundColor Red
    }
}

Write-Host "`n==============================================" -ForegroundColor Cyan
Write-Host "Lab Execution Complete!" -ForegroundColor Cyan
Write-Host "Dashboard UI: http://localhost:8080/" -ForegroundColor Cyan
Write-Host "Swagger UI:   http://localhost:8080/swagger-ui.html" -ForegroundColor Cyan
