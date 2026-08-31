Write-Host "==============================================" -ForegroundColor Cyan
Write-Host "Autocomplete Search System (Trie & Redis)" -ForegroundColor Cyan
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
while ($(curl -s -o /dev/null -w "%{http_code}" http://localhost:8087/api/autocomplete/cache/stats 2>$null) -notmatch "200") {
    Start-Sleep -Seconds 2
    Write-Host "." -NoNewline
}
Write-Host " Ready!" -ForegroundColor Green

Write-Host "`n>>> DEMO: SEARCH 'app' (CACHE MISS) <<<" -ForegroundColor Yellow
Invoke-RestMethod -Uri "http://localhost:8087/api/autocomplete/search?q=app&k=5"

Write-Host "`n>>> DEMO: SEARCH 'app' (CACHE HIT) <<<" -ForegroundColor Yellow
Invoke-RestMethod -Uri "http://localhost:8087/api/autocomplete/search?q=app&k=5"

Write-Host "`n==============================================" -ForegroundColor Cyan
Write-Host "Lab Execution Complete!" -ForegroundColor Cyan
Write-Host "Dashboard UI: http://localhost:8087/" -ForegroundColor Cyan
Write-Host "Swagger UI:   http://localhost:8087/swagger-ui.html" -ForegroundColor Cyan
