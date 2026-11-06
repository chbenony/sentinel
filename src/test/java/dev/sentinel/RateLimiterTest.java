package dev.sentinel;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.LongSupplier;

import static org.junit.jupiter.api.Assertions.*;

public class RateLimiterTest {

    @Test
    void unknownPolicyThrows(){
        LongSupplier clock = () -> 0L;
        PolicyRegistry registry = new InMemoryPolicyRegistry(Map.of());
        InMemoryBucketStore store = new InMemoryBucketStore(clock);

        RateLimiter rateLimiter = new RateLimiter(registry, store);


        assertThrows(UnknownPolicyException.class, () -> rateLimiter.check(new LimitKey("app_1", "res_1", "princ_1"), 1));
    }

    @Test
    void knownPolicyAllowsRequest() {
        LongSupplier clock = () -> 20L;

        PolicyKey policyKey = new PolicyKey("app_1", "res_1");
        Policy policy = new Policy(2, 2.0);

        PolicyRegistry registry = new InMemoryPolicyRegistry(Map.of(policyKey, policy));
        InMemoryBucketStore bucketStore = new InMemoryBucketStore(clock);
        LimitKey limitKey = new LimitKey("app_1", "res_1", "princ_1");

        RateLimiter rateLimiter = new RateLimiter(registry, bucketStore);
        Decision decision = rateLimiter.check(limitKey, 2);

        assertTrue(decision.allowed());
    }

    @Test
    void principalsHaveSeparateLimits() {
        LongSupplier clock = () -> 2L;

        PolicyKey policyKey = new PolicyKey("app_1", "res_1");
        Policy policy = new Policy(1, 2.0);

        PolicyRegistry registry = new InMemoryPolicyRegistry(Map.of(policyKey, policy));
        InMemoryBucketStore bucketStore = new InMemoryBucketStore(clock);
        LimitKey limitKey = new LimitKey(policyKey.application(), policyKey.resource(), "princ_1");
        LimitKey limitKey2 = new LimitKey(policyKey.application(), policyKey.resource(), "princ_2");

        RateLimiter rateLimiter = new RateLimiter(registry, bucketStore);

        Decision alice1 = rateLimiter.check(limitKey, 1);
        Decision alice2 = rateLimiter.check(limitKey, 1);

        Decision bob1 = rateLimiter.check(limitKey2, 1);
        Decision bob2 = rateLimiter.check(limitKey2, 1);

        assertTrue(alice1.allowed());
        assertFalse(alice2.allowed());

        assertTrue(bob1.allowed());
        assertFalse(bob2.allowed());
    }
}
