# 5. Fail closed on unknown policies

- **Status:** Accepted
- **Date:** 2026-11-06

## Context

A request may name an `(application, resource)` that has no configured policy (a typo, or a new client that isn't configured yet).

## Decision

- `PolicyRegistry.find` returns `Optional<Policy>`. To the registry, "not found" is a normal answer, not an error.
- `RateLimiter.check` treats a missing policy as an error and throws `UnknownPolicyException`; the HTTP layer returns **404**.

## Alternatives considered

- **Fail open with a default policy:** any typo or unknown client silently gets limits nobody chose.
- **Return a denied `Decision`:** lies about what happened. The client would back off and retry forever, and there's no meaningful `retryAfter`.
- **`findOrCreate` for policies** (like buckets): policies are configuration, not runtime state, so there's nothing sensible to create.

## Consequences

- Misconfiguration is loud and immediate, and it's distinguishable from rate limiting.
- Every new client needs a policy before it can be served.