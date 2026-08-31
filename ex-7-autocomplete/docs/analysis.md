# Analysis — Autocomplete Search System using Prefix Matching

**UCS3513 System Design Laboratory** — Exercise 7 Analysis & Lab Report

---

## 1. Role of Components

### Trie (Prefix Tree)
A **Trie** is a specialized tree-based data structure used to store associative data structures where keys are usually strings. In an autocomplete search system, each node of the Trie represents a character of a search term. Storing terms in a Trie allows prefix-based lookups in $O(L)$ time, where $L$ is the length of the prefix string, regardless of total dataset size.

### Prefix
A **Prefix** is the initial substring entered by a user into the search bar (e.g. `"app"` for terms like `"apple"`, `"application"`, `"appointment"`). The system uses the prefix to traverse the Trie to a prefix root node and retrieve candidate search suggestions branching from that node.

### Redis Cache
**Redis** is an in-memory key-value cache used to store previously computed top-K search suggestions for popular prefixes (Key: `autocomplete:<prefix>:<topK>`). Serving requests directly from Redis bypassing the Trie yields sub-millisecond response times ($\approx 1\text{ ms}$) and reduces CPU utilization under heavy search traffic.

### Top-K Suggestions
**Top-K Suggestions** represents the top $K$ most relevant and frequent search queries matching a prefix (e.g., $K = 5$). Terms matching the prefix are sorted descending by historical search frequency so users see the most popular queries first.

---

## 2. How the Trie Determines Search Suggestions for a Given Prefix

1. **Prefix Traversal**: Starting at the root node, the system traverses down the Trie character by character matching the input prefix. If a character does not exist as a child node, no terms match the prefix and an empty list is returned immediately in $O(L)$ time.
2. **Subtree Collection**: Once the node corresponding to the last character of the prefix is reached, a Depth-First Search (DFS) or Breadth-First Search (BFS) is performed on the entire subtree rooted at that node to collect all valid word nodes.
3. **Frequency Sorting & Truncation**: The collected candidate words are sorted in descending order of search frequency. The system extracts the top $K$ highest-frequency items to form the suggestion response.

```
                    Root
                    /  \
                   a    g ...
                  /
                 p
                /
               p  <-- Prefix Node "app"
             / | \
            l  o  m
           /   |   \
          e    r    a ...
       (apple) (app store)
```

---

## 3. Request Processing Flow when Prefix is Available in Redis (Cache HIT)

```
User Query ("app") ──> API Controller ──> Check Redis ("autocomplete:app:5")
                                                        │
                                                        ▼
                                                  CACHE HIT! (1 ms)
                                                        │
                                                        ▼
                                        Return Suggestions + Header (X-Cache: HIT)
```

1. Client sends request `GET /api/autocomplete/search?q=app&k=5`.
2. `AutocompleteService` constructs cache key `autocomplete:app:5`.
3. System checks Redis cache.
4. **Cache Hit**: Cached JSON array of `SuggestionDto` items is retrieved directly from Redis memory.
5. Service increments `cacheHits` counter and returns response with header `X-Cache: HIT` within $\approx 1\text{ ms}$.

---

## 4. Request Processing Flow when Prefix is Not Available in Redis (Cache MISS)

```
User Query ("app") ──> API Controller ──> Check Redis ("autocomplete:app:5")
                                                        │
                                                        ▼
                                                 CACHE MISS!
                                                        │
                                                        ▼
                                             Traverse Trie Subtree
                                                        │
                                                        ▼
                                            Sort Top-K by Frequency
                                                        │
                                                        ▼
                                            Store in Redis (TTL = 10m)
                                                        │
                                                        ▼
                                        Return Suggestions + Header (X-Cache: MISS)
```

1. Client sends request `GET /api/autocomplete/search?q=app&k=5`.
2. System checks Redis for key `autocomplete:app:5` $\rightarrow$ Returns null (**Cache Miss**).
3. Service increments `cacheMisses` counter.
4. `Trie.searchPrefix("app", 5)` is executed: traverses Trie to prefix node `"app"`, collects subtree terms, sorts by frequency, and returns top 5 matches.
5. The result list is stored into Redis with a 10-minute TTL (`Duration.ofMinutes(10)`).
6. Response is returned to client with header `X-Cache: MISS`. Subsequence requests for `"app"` will hit Redis.

---

## 5. Comparison: Trie-Based Prefix Search vs. Linear Search

| Metric | Linear Search (`LIKE 'prefix%'`) | Trie-Based Prefix Search | Trie + Redis Cache |
|--------|----------------------------------|--------------------------|--------------------|
| **Lookup Time Complexity** | $O(N \cdot L)$ (Scans all $N$ records) | $O(L + M \log M)$ ($L$ = prefix len, $M$ = candidates) | **$O(1)$** (Direct key lookup) |
| **Response Time** | High ($50\text{ ms} - 500\text{ ms}$ on large DBs) | Fast ($2\text{ ms} - 5\text{ ms}$) | **Ultra-Fast ($< 1\text{ ms}$)** |
| **Database Load** | High CPU & Disk I/O | In-memory Trie traversal | Minimal (bypasses Trie) |
| **Scalability** | Poor under high search query volume | High in-memory throughput | **Massive (handles 100k+ QPS)** |
| **Memory Footprint** | Low (database index) | Moderate (tree node pointers) | Moderate (cached hot prefixes) |
