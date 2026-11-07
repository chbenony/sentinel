# 8. JDK `HttpServer` + Jackson for the HTTP layer

- **Status:** Accepted
- **Date:** 2026-11-04

## Context

v1 needs exactly one endpoint, `POST /v1/limits/check`, with JSON in and out.

## Decision

- Use the JDK's built-in `com.sun.net.httpserver.HttpServer` with a **virtual-thread-per-request** executor.
- Use **Jackson 3** (`tools.jackson`) for JSON, mapping straight onto records.
- A shared `CheckHandler.defaultJsonMapper()` configures Jackson (e.g. rejects `1.9` as a cost instead of truncating it).

## Alternatives considered

- **Spring Boot:** dependency injection, routing, and validation we don't need for one endpoint, plus slower startup. Can be revisited as an HTTP-layer rewrite once the core is mature.
- **Hand-written JSON parsing:** zero dependencies, but a fragile mini-parser.
- **Default single-thread executor:** handles requests one at a time.

## Consequences

- Tiny dependency footprint: Jackson is the only runtime dependency.
- More manual code: method checks, status codes, closing the response stream.
- The core (`RateLimiter` and below) has no HTTP dependency, so the HTTP layer is replaceable.