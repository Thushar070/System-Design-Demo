# Exercise 3 — URL Shortener with Redis Caching

## Step 1: Start Database Prerequisites

Start Redis:
```bash
redis-server --daemonize yes
```

Start MongoDB on port 27017:
```bash
docker start 274c1ace4804 || docker run -d -p 27017:27017 --name mongodb mongo:7.0
```

---

## Step 2: Run Backend Service

```bash
cd /home/billy/Data/Projects/System-Design-Exercises/ex-3-url-shortener-redis/backend
chmod +x mvnw
./mvnw spring-boot:run
```

---

## Step 3: Run Frontend Interface

If port 3000 is already in use, free port 3000:
```bash
fuser -k 3000/tcp
```

Serve the frontend from the frontend directory:
```bash
cd /home/billy/Data/Projects/System-Design-Exercises/ex-3-url-shortener-redis/frontend
python3 -m http.server 3000
```

Open http://localhost:3000 in your web browser.
