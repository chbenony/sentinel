# 9. `/v1/limits/check` API contract

- **Status:** Accepted
- **Date:** 2026-11-06

## Context

Clients (starting with Sift, written in Go) need to tell "you're rate limited" apart from "Sentinel can't answer".

## Decision

**Status codes**

| Case | Status |
|---|---|
| Allowed **or denied** | `200` + body |
| Malformed JSON, missing/blank fields, invalid or fractional cost, `null` body | `400` |
| Unknown policy | `404` |
| Any method other than POST | `405` + `Allow: POST` |

**Response body:** `{"allowed": bool, "remaining": int, "retryAfterMillis": long}`

- A denial is a successful answer to "may I?", so it's `200`, with `allowed: false`.
- `retryAfterMillis` is a plain number with the unit in its name, rounded **up** from nanoseconds (`Math.ceilDiv`), so a sub-millisecond wait is never reported as `0`.

## Alternatives considered

- **`429` for denied:** would mean Sentinel is rate limiting *Sift*. Clients would have to treat one non-2xx code as a normal answer.
- **`204` for allowed:** no body, but `remaining` is useful.
- **Serializing `Duration` directly:** Jackson writes ISO-8601 (`"PT0.5S"`), awkward for Go clients.
- **Truncating to millis:** a 0.3 ms wait becomes `0`, and clients busy-loop.

## Consequences

- Client logic is simple: `200` → read `allowed`; anything else → Sentinel problem.
- Validation happens at the boundary (`CheckRequest`'s compact constructor) **and** in the core (`TokenBucket`), because they protect different things: wire format vs the algorithm's invariants.