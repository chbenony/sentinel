package dev.sentinel;


import java.util.Optional;

public class RateLimiter {
    private final PolicyRegistry policyRegistry;
    private final BucketStore bucketStore;


    public RateLimiter(PolicyRegistry policyRegistry, BucketStore bucketStore) {
        this.policyRegistry = policyRegistry;
        this.bucketStore = bucketStore;
    }

    public Decision check(LimitKey key, int cost) {

        PolicyKey policyKey = new PolicyKey(key.application(), key.resource());
        Optional<Policy> policyOptional = policyRegistry.find(policyKey);

        Policy policy = policyOptional.orElseThrow(() -> new UnknownPolicyException(policyKey));

        TokenBucket bucket = bucketStore.findOrCreate(key, policy);

        return bucket.tryConsume(cost);
    }
}
