local key = KEYS[1]
local capacity = tonumber(ARGV[1])
local refill_rate = tonumber(ARGV[2])
local cost = tonumber(ARGV[3])
local now = tonumber(ARGV[4])

local data = redis.call('HMGET', key, 'tokens', 'last_refill')
local tokens = tonumber(data[1])
local last_refill = tonumber(data[2])

if tokens == nil or last_refill == nil then
    tokens = capacity
    last_refill = now
else
    local delta = math.max(0, now - last_refill)
    local tokens_to_add = delta * refill_rate
    tokens = math.min(capacity, tokens + tokens_to_add)
    last_refill = now
end

local allowed = 0

if tokens >= cost then
    allowed = 1
    tokens = tokens - cost
else
    allowed = 0
end

redis.call('HMSET', key, 'tokens', tokens, 'last_refill', last_refill)
local ttl = math.ceil(capacity / math.max(1, refill_rate)) + 60
redis.call('EXPIRE', key, ttl)

return { allowed, math.floor(tokens), capacity }
