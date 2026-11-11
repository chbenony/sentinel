# Sentinel

**Sentinel** is a protocol-agnostic rate-limiting service. Clients ask *"may this principal spend `cost` on this resource right now?"* and Sentinel answers with a token-bucket decision.

It is built for [Sift](https://github.com/chbenony/sift) (an AI gateway with its own UI that Écoute also uses), but it knows nothing about AI, MCP, or any client's domain: just `principal`, `application`, `resource`, and `cost`.

## Status

**v0.1** — a single node with in-memory state.

- ✅ Thread-safe token bucket with lazy refill, covered by concurrency tests
- ✅ `POST /v1/limits/check` on the JDK `HttpServer` with virtual threads
- ✅ Fails closed on unknown policies
- ⏳ Policies are hard-coded in `App.main` (config file next)
- ⏳ Shared state across instances (Redis) — until then, run one instance
- ⏳ Metrics and client authentication

## Quick start

Requires JDK 25 (Gradle's toolchain support can download it for you).

```sh
./gradlew run
```

In another terminal:

```sh
curl -i -X POST localhost:8080/v1/limits/check \
  -d '{"principal":"alice","application":"sift","resource":"chat","cost":1}'
```

```json
{"allowed":true,"remaining":99,"retryAfterMillis":0}
```

The built-in policy for `sift`/`chat` allows a burst of 100 and refills 10 tokens per second.

## API

`POST /v1/limits/check`

| Request field | Type | Notes |
|---|---|---|
| `principal` | string | Who is spending (user, API key, …). Each principal gets its own bucket. |
| `application` | string | Together with `resource`, selects the policy. |
| `resource` | string | |
| `cost` | integer | Tokens to spend; must be ≥ 1 and ≤ the policy's capacity. |

| Response field | Type | Notes |
|---|---|---|
| `allowed` | boolean | Whether the request may proceed. Tokens are spent only if `true`. |
| `remaining` | integer | Tokens left in the bucket (floored). |
| `retryAfterMillis` | integer | When denied, how long until `cost` tokens are available (rounded up). `0` when allowed. |

| Status | Meaning |
|---|---|
| `200` | A decision was made — **both allowed and denied**. Check `allowed`. |
| `400` | Malformed JSON, missing/blank fields, or an invalid cost |
| `404` | No policy for this `application`/`resource` |
| `405` | Method other than `POST` |

Why a denial is a `200` and not a `429`: see [ADR 9](docs/decisions/0009-check-api-contract.md).

## Development

```sh
./gradlew test    # unit, concurrency, and HTTP tests
./gradlew build
```

## Docs

- [Architecture diagram](docs/architecture.md)
- [Architecture decision records](docs/decisions/README.md) — what was chosen, what was rejected, and why
