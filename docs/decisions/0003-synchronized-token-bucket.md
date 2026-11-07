# 3. `synchronized` for thread safety in v1

- **Status:** Accepted
- **Date:** 2026-11-04

## Context

`tryConsume` is a read-modify-write on two fields (`currentTokens`, `lastRefillTime`). Without synchronization, a concurrency test with 8 threads let **200,000** requests through a 100,000-token bucket: lost updates, plus the JIT keeping non-volatile fields in registers.

## Decision

Make `tryConsume` `synchronized`. It gives mutual exclusion and a happens-before edge between consecutive callers, so every thread sees the latest state.

## Alternatives considered

- **`volatile` fields:** fixes visibility, not atomicity. Two threads can still both read "1 token left".
- **`ReentrantLock`:** same guarantees with more features (tryLock, timeouts) we don't need yet.
- **Lock-free CAS** on an immutable state object: highest throughput under contention, but more complex.

## Consequences

- Correct, simple, and verified: the concurrency test fails without `synchronized` and passes with it.
- On JDK 24+ (JEP 491), `synchronized` no longer pins virtual threads, so it works with the virtual-thread HTTP server.
- `ReentrantLock` and CAS versions are planned as learning exercises against the same tests.