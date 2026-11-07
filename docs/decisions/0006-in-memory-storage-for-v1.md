# 6. In-memory storage for v1, Redis when scaling out

- **Status:** Accepted
- **Date:** 2026-11-05

## Context

Buckets are runtime state, created on demand for each new principal. They are small, hot, and only need to live as long as the rate window.

## Decision

- v1 stores buckets in a `ConcurrentHashMap<LimitKey, TokenBucket>` behind a `BucketStore` interface.
- `computeIfAbsent` guarantees at most one bucket per key, even when many threads hit a new key at once.
- Move to **Redis** when running more than one Sentinel instance.
- A real database is reserved for billing/token quotas (see [ADR 1](0001-protocol-agnostic-core.md)), not request-rate buckets.

## Alternatives considered

- **Redis from day one:** network hop and operational cost before there's a second instance to share state with.
- **A relational database:** durability isn't needed for state that refills in seconds, and the write rate is one per request.
- **`get` then `put` on a plain map:** racy; two threads can each create a bucket and one loses its tokens.

## Consequences

- Fast (one map lookup + one lock) and simple.
- State is lost on restart, which means everyone gets a full bucket. Acceptable for rate limiting.
- Two instances would each count separately, which effectively doubles the limit. That's the signal to move to Redis.
- No eviction of idle buckets yet; memory grows with distinct principals.