# 1. Protocol-agnostic core, request rates only

- **Status:** Accepted
- **Date:** 2026-11-01

## Context

Sentinel will serve several clients: Sift (Go AI gateway), Écoute (Go/React, routed through Sift), and future MCP servers. Each speaks about its own concepts (chat requests, tools, tokens, billing).

## Decision

The core knows only four things: `principal`, `application`, `resource`, and `cost`. It limits **request rates**. It has no knowledge of MCP, AI tokens, or billing.

## Alternatives considered

- **Client-specific concepts in the core** (e.g. MCP tool names, model names): couples Sentinel to each client's domain; every new client means core changes.
- **One system for request rates *and* AI token/billing quotas:** quotas need durable, auditable storage and different semantics (monthly budgets, not refill rates). Mixing them would force the worst-case storage onto every request.

## Consequences

- Any client can integrate by mapping its concepts onto the four fields.
- Billing/token quotas will be a separate component later, with a real database.