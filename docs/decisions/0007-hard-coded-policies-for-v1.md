# 7. Hard-coded policies behind an interface for v1

- **Status:** Accepted (to be revisited when Sift integrates)
- **Date:** 2026-11-06

## Context

v1 has one real client (Sift) and one policy (`sift`/`chat`: capacity 100, 10/s).

## Decision

Policies are a `Map.of(...)` built in `App.main` and passed to `InMemoryPolicyRegistry`. Everything else depends only on the `PolicyRegistry` interface.

The registry stores `Map.copyOf(...)` of what it's given, so it's immutable and safe to read from any thread, and later changes to the caller's map have no effect.

## Alternatives considered

- **Config file or environment variables now:** a loader, a format, and error handling for a single policy. YAGNI.
- **A database:** policies change rarely; a DB adds a dependency for no benefit yet.

## Consequences

- Changing a policy means a code change and redeploy.
- Swapping in a `FilePolicyRegistry` later is a one-line change in `main`.
- `Map.copyOf` also rejects null keys or values, so bad config fails at startup.