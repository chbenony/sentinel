# Architecture
```mermaid
flowchart LR
    Sift-->|"POST /v1/limits/check"|CheckHandler
    subgraph Sentinel
        CheckHandler-->RateLimiter
        RateLimiter-->|PolicyKey|PolicyRegistry
        RateLimiter-->|LimitKey|BucketStore
        BucketStore -->TokenBucket
    end
```