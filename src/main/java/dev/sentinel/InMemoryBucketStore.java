package dev.sentinel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.LongSupplier;

public class InMemoryBucketStore implements BucketStore{
    private final ConcurrentHashMap<LimitKey, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final LongSupplier clock;

    public InMemoryBucketStore(LongSupplier clock){
        this.clock = clock;
    }

    @Override
    public TokenBucket findOrCreate(LimitKey key, Policy policy) {
       return buckets.computeIfAbsent(key, k -> new TokenBucket(policy.capacity(), policy.refillRate(), clock));
    }
}
