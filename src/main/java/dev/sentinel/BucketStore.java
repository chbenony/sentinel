package dev.sentinel;

public interface BucketStore {
    TokenBucket findOrCreate(LimitKey key, Policy policy);
}
