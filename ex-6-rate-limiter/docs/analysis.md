# Analysis — API Rate Limiter using Token Bucket and Redis

**UCS3513 System Design Laboratory** — Exercise 6 Analysis & Lab Report

---

## 1. Role of Components

### Token Bucket
The **Token Bucket** is a rate-limiting algorithm mechanism that controls API traffic volume by modeling request permissions as tokens stored inside a container (bucket). Tokens are added to the bucket periodically at a fixed rate. When an API request arrives, the system attempts to draw a token from the bucket:
- If a token is available, the token is consumed and the request is **allowed** (HTTP 200).
- If the bucket is empty, no token can be drawn and the request is **rejected** (HTTP 429 Too Many Requests).

### Bucket Capacity ($C$)
**Bucket Capacity** specifies the maximum number of tokens that the bucket can hold at any given moment (e.g., $C = 10$). It dictates the **maximum burst capacity** of requests that a client can execute instantaneously when the bucket is full. Any tokens generated beyond this capacity overflow and are discarded.

### Refill Rate ($r$)
The **Refill Rate** determines how fast new tokens are added back to the bucket over time (e.g., $r = 2$ tokens per second). It sets the **sustained request rate limit** for a client over long durations.

### Redis
**Redis** acts as the centralized, in-memory distributed data store for token bucket states (`tokens` count and `last_refill` timestamp) across multiple application instances. By executing the Token Bucket algorithm inside Redis using **Lua scripting**, atomicity is guaranteed, preventing race conditions under high concurrent traffic.

---

## 2. How Token Bucket Allows or Rejects Requests

When a client makes a request with identifier `client_id`:

1. **State Fetch & Lazy Refill**:
   - The system retrieves the saved state (`tokens` and `last_refill` epoch timestamp) from Redis.
   - It computes elapsed time $\Delta t = \text{now} - \text{last\_refill}$.
   - It calculates refilled tokens: $\text{tokens}_{\text{new}} = \min(\text{Capacity}, \text{tokens} + \Delta t \times r)$.

2. **Evaluation & Decision**:
   - **Allowed ($\text{tokens} \ge \text{cost}$)**: The system deducts the requested cost (usually $1$ token), updates Redis with the new token count and `last_refill = now`, adds response headers (`X-RateLimit-Limit`, `X-RateLimit-Remaining`), and routes the request to the backend controller.
   - **Rejected ($\text{tokens} < \text{cost}$)**: The request is aborted immediately with **HTTP 429 Too Many Requests**, returning headers `Retry-After: 1` and `X-RateLimit-Remaining: 0`.

```
                    Incoming Request (client_id)
                                 │
                                 ▼
                     Calculate Refill Tokens
             tokens = min(Capacity, tokens + Δt * refillRate)
                                 │
                       Is tokens >= cost?
                             /       \
                      YES   /         \   NO
                           v           v
                  Deduct 1 Token    Reject Request
                  HTTP 200 OK       HTTP 429 Too Many Requests
```

---

## 3. Why Redis is Required with Multiple Application Instances

In a distributed web application with multiple Spring Boot application nodes behind a load balancer (e.g., Node A, Node B, Node C):

1. **In-Memory Isolation Problem**: If rate-limiting state were kept in local JVM memory (e.g., local `HashMap`), a client sending 10 requests split evenly across 3 nodes would consume only ~3 tokens per node rather than 10 total tokens. The client could bypass rate limits by distributing requests across nodes.
2. **Centralized Atomic State**: Storing token states in **Redis** ensures that all application instances query and update the exact same centralized source of truth.
3. **Atomic Lua Scripting**: Redis single-threaded execution model paired with Lua scripts guarantees that token evaluation and deduction are atomic operations, eliminating race conditions across application instances.

---

## 4. Comparison of Rate Limiting Algorithms

| Algorithm | How it Works | Pros | Cons | Burst Handling |
|-----------|--------------|------|------|----------------|
| **Fixed Window** | Divides time into fixed windows (e.g. 1 min). Resets count at window boundaries. | Memory efficient, simple to implement. | Boundary burst vulnerability (2x traffic at window reset edges). | Poor (suffers edge bursts) |
| **Sliding Window Log** | Keeps timestamp log of all requests in a sorted set (ZSET). Counts entries in past window. | 100% accurate, no edge burst issues. | High memory footprint (stores timestamp for every request). | Moderate |
| **Sliding Window Counter** | Combines current window count + weighted previous window count. | Memory efficient, smooths edge spikes. | Approximation (assumes uniform rate in previous window). | Good |
| **Token Bucket** (Used in Lab) | Tokens accumulate up to capacity $C$ at refill rate $r$. Each request consumes 1 token. | Allows controlled traffic bursts up to capacity $C$ while maintaining average rate $r$. | Slightly higher logic complexity. | **Ideal for Web APIs & Bursts** |

---

## 5. System Behaviour During Normal vs. Burst Traffic

### Normal Traffic
- Requests arrive at a steady rate below the refill rate (e.g. 1 request every 2 seconds when refill rate = 2/sec).
- The bucket remains nearly full ($\text{tokens} \approx \text{Capacity}$).
- Every request succeeds with **HTTP 200 OK**, maintaining low latency and 0% drop rate.

### Burst Traffic
- A client fires an instantaneous burst of 15 requests within 200ms when capacity $C = 10$.
- **First 10 Requests**: Consume all available 10 tokens $\rightarrow$ **HTTP 200 OK**.
- **Next 5 Requests**: Bucket is completely depleted (0 tokens) $\rightarrow$ **HTTP 429 Too Many Requests** with `Retry-After: 1`.
- **Refill Phase**: After 1 second, $2$ tokens are replenished into the bucket, allowing the client to execute 2 new requests.
