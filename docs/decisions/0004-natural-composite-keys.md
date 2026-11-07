# 4. Natural composite keys as records

- **Status:** Accepted
- **Date:** 2026-11-05

## Context

Each bucket belongs to one `(application, resource, principal)`. Policies apply per `(application, resource)`. We need map keys for both.

## Decision

Use records as natural composite keys: `LimitKey(application, resource, principal)` and `PolicyKey(application, resource)`. Records give correct `equals`/`hashCode` and immutability for free, so they are safe map keys.

## Alternatives considered

- **Surrogate IDs** (generate an ID per bucket, look it up): the database instinct. But every request already carries the three identifying fields, so an ID adds a second lookup and a mapping to keep in sync, with no benefit.
- **String concatenation** (`app + ":" + resource`): collisions when a value contains the separator.
- **Two `String` parameters** for policy lookup: easy to pass in the wrong order, and still needs a composite key internally.
- **Reusing `LimitKey` for policies:** the principal would be silently ignored, which is misleading.

## Consequences

- Lookup is a single hash-map access.
- Redis keys later can be derived from the same fields.