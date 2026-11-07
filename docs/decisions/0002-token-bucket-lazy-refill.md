# 2. Token bucket with lazy refill and an injected clock

- **Status:** Accepted
- **Date:** 2026-11-03

## Context

We need an algorithm that allows short bursts but enforces an average rate, and that is easy to test deterministically.

## Decision

- **Token bucket** with `capacity` (burst size) and `refillRate` (tokens per second).
- **Lazy refill:** no background timer. On each `tryConsume`, compute `elapsed × refillRate` since the last call and top up, capped at `capacity`.
- Tokens are a `double`, so fractional refill isn't lost between calls. `remaining` is floored to an `int` for callers.
- `retryAfter` is rounded **up** (`Math.ceil`), never down, so clients don't retry too early.
- The clock is injected as a `LongSupplier` (nanoseconds): `System::nanoTime` in production, a fake field-backed clock in tests.

## Alternatives considered

- **Fixed window counters:** simpler, but allow 2× bursts at window boundaries.
- **Sliding log:** precise, but stores every request timestamp.
- **Background refill thread:** extra moving parts, and wasted work for idle buckets.
- **`System.nanoTime()` called directly:** makes time-based tests slow and flaky.

## Consequences

- Idle buckets cost nothing until their next request.
- Tests control time exactly (`now += 500_000_000`) instead of sleeping.