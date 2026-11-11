# Architecture Decision Records

Short records of the significant design decisions in Sentinel: what was chosen, what was rejected, and why.

Each record follows: **Context → Decision → Alternatives considered → Consequences.** To add one, copy the latest file, bump the number, and add a line below. If a decision is replaced, mark the old record "Superseded by N" rather than deleting it.

See the [architecture diagram](../architecture.md) for how the pieces fit together.

| # | Decision | Status |
|---|---|---|
| [1](0001-protocol-agnostic-core.md) | Protocol-agnostic core, request rates only | Accepted |
| [2](0002-token-bucket-lazy-refill.md) | Token bucket with lazy refill and an injected clock | Accepted |
| [3](0003-synchronized-token-bucket.md) | `synchronized` for thread safety in v1 | Accepted |
| [4](0004-natural-composite-keys.md) | Natural composite keys as records | Accepted |
| [5](0005-fail-closed-on-unknown-policy.md) | Fail closed on unknown policies | Accepted |
| [6](0006-in-memory-storage-for-v1.md) | In-memory storage for v1, Redis when scaling out | Accepted |
| [7](0007-hard-coded-policies-for-v1.md) | Hard-coded policies behind an interface for v1 | Accepted |
| [8](0008-jdk-httpserver-and-jackson.md) | JDK `HttpServer` + Jackson for the HTTP layer | Accepted |
| [9](0009-check-api-contract.md) | `/v1/limits/check` API contract | Accepted |